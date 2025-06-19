package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.repository.JobQueueRepository;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.function.Function;
import java.util.stream.Collectors;

@Component
@Slf4j
public class JobConsumer {
    private final JobQueueRepository      jobQueueRepository;
    private final Map<String, JobHandler> jobHandlers;
    private final ExecutorService         executorService;

    @Autowired
    public JobConsumer(JobQueueRepository jobQueueRepository, List<JobHandler> handlers) {
        this.jobQueueRepository = jobQueueRepository;
        this.jobHandlers = handlers.stream()
                .collect(Collectors.toMap(JobHandler::getJobType, Function.identity()));
        // 可配置化
        this.executorService = Executors.newFixedThreadPool(5);
    }

    @PostConstruct
    public void init() {
        // 启动3个worker线程
        startWorkerThreads(3);
    }

    private void startWorkerThreads(int numThreads) {
        for (int i = 0; i < numThreads; i++) {
            executorService.execute(this::processJobs);
        }
    }

    private void processJobs() {
        while (!Thread.currentThread().isInterrupted()) {
            try {
                JobQueue job = fetchAndLockNextJob();
                if (job != null) {
                    processSingleJob(job);
                } else {
                    Thread.sleep(1000); // 没有作业时休眠
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Job processing thread interrupted");
            } catch (Exception e) {
                log.error("Error processing job", e);
            }
        }
    }

    @Transactional
    public JobQueue fetchAndLockNextJob() {
        // 使用Native Query确保SKIP LOCKED生效
        JobQueue job = jobQueueRepository.findNextPendingJob();
        if (job != null) {
            job.markAsProcessing();
            jobQueueRepository.save(job);
        }
        return job;
    }

    private void processSingleJob(JobQueue job) {
        try {
            JobHandler handler = jobHandlers.get(job.getJobType());
            if (handler != null) {
                handler.handle(job);
                markJobAsCompleted(job);
            } else {
                markJobAsFailed(job, "No handler found for job type: " + job.getJobType());
            }
        } catch (Exception e) {
            markJobAsFailed(job, e.getMessage());
            log.error("Failed to process job {}: {}", job.getId(), e.getMessage(), e);
        }
    }

    @Transactional
    public void markJobAsCompleted(JobQueue job) {
        job.markAsCompleted();
        jobQueueRepository.save(job);
        log.info("Job {} completed successfully", job.getId());
    }

    @Transactional
    public void markJobAsFailed(JobQueue job, String error) {
        job.markAsFailed();
        // 可以添加错误信息到payload
        jobQueueRepository.save(job);
        log.error("Job {} failed: {}", job.getId(), error);
    }

    // 批量处理作业
    @Transactional
    public void processBatchJobs(int batchSize) {
        List<JobQueue> jobs = jobQueueRepository.findPendingJobs(batchSize);
        jobs.forEach(job -> {
            try {
                JobHandler handler = jobHandlers.get(job.getJobType());
                if (handler != null) {
                    job.markAsProcessing();
                    jobQueueRepository.save(job);

                    handler.handle(job);
                    markJobAsCompleted(job);
                } else {
                    markJobAsFailed(job, "No handler found");
                }
            } catch (Exception e) {
                markJobAsFailed(job, e.getMessage());
            }
        });
    }
}