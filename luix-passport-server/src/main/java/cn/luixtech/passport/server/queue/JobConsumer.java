package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.repository.JobQueueRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

@Component
@Slf4j
public class JobConsumer {
    private static final Map<String, JobHandler> JOB_HANDLERS = new ConcurrentHashMap<>();
    private final        JobQueueRepository      jobQueueRepository;
    private final        ExecutorService         executorService;
    private volatile     boolean                 running      = true;

    // 配置参数
    private static final int WORKER_THREADS   = 3;
    private static final int POLL_INTERVAL_MS = 1000;
    private static final int BATCH_SIZE       = 5;

    @Autowired
    public JobConsumer(JobQueueRepository jobQueueRepository) {
        this.jobQueueRepository = jobQueueRepository;
        this.executorService = Executors.newFixedThreadPool(WORKER_THREADS);
    }

    @PostConstruct
    public void start() {
        for (int i = 0; i < WORKER_THREADS; i++) {
            executorService.execute(this::processJobs);
        }
        log.info("Started {} job consumer threads", WORKER_THREADS);
    }

    @PreDestroy
    public void shutdown() {
        running = false;
        executorService.shutdown();
        try {
            if (!executorService.awaitTermination(10, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Job consumer stopped");
    }

    /**
     * 注册单个作业处理器
     *
     * @param handler 作业处理器实例
     */
    public void registerHandler(JobHandler handler) {
        if (handler != null) {
            JOB_HANDLERS.put(handler.getJobType(), handler);
            log.info("Registered job handler for type: {}", handler.getJobType());
        }
    }

    /**
     * 批量注册作业处理器
     *
     * @param handlers 作业处理器列表
     */
    public void registerHandlers(List<JobHandler> handlers) {
        if (handlers != null) {
            handlers.forEach(this::registerHandler);
        }
    }

    /**
     * 移除作业处理器
     *
     * @param jobType 作业类型
     */
    public void unregisterHandler(String jobType) {
        if (jobType != null) {
            JOB_HANDLERS.remove(jobType);
            log.info("Unregistered job handler for type: {}", jobType);
        }
    }

    @Transactional
    public void processJobs() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // 优先处理批量作业
                if (processBatchJobs(BATCH_SIZE)) {
                    continue;
                }

                // 没有批量作业时处理单个作业
                Optional<JobQueue> jobOpt = fetchAndProcessSingleJob();
                if (jobOpt.isEmpty()) {
                    Thread.sleep(POLL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
                log.info("Job processing thread interrupted");
            } catch (Exception e) {
                log.error("Error in job processing", e);
                sleepToPreventSpin();
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<JobQueue> fetchAndProcessSingleJob() {
        try {
            Optional<JobQueue> jobOpt = jobQueueRepository.lockAndGetNextPendingJob();

            if (jobOpt.isEmpty()) {
                return Optional.empty();
            }

            JobQueue job = jobOpt.get();
            processJobWithHandler(job); // 处理可能在内部开新事务
            return Optional.of(job);

        } catch (Exception e) {
            // 这里不捕获异常，让事务回滚
            // 锁定的作业会因为事务回滚自动恢复为pending状态
            throw new RuntimeException("Failed to process job", e);
        }
    }

    @Transactional
    public boolean processBatchJobs(int batchSize) {
        List<JobQueue> jobs = jobQueueRepository.lockAndGetNextPendingJobs(batchSize);
        if (jobs.isEmpty()) {
            return false;
        }

        jobs.forEach(job -> {
            try {
                processJobWithHandler(job);
            } catch (Exception e) {
                markJobAsFailed(job, e.getMessage());
                log.error("Failed to process job {}: {}", job.getId(), e.getMessage());
            }
        });
        return true;
    }

    private void processJobWithHandler(JobQueue job) throws Exception {
        JobHandler handler = JOB_HANDLERS.get(job.getJobType());
        if (handler == null) {
            throw new RuntimeException("No handler found for job type: " + job.getJobType());
        }

        log.info("Processing job {} of type {}", job.getId(), job.getJobType());
        handler.accept(job);
        markJobAsCompleted(job);
    }

    private void markJobAsCompleted(JobQueue job) {
        job.markAsCompleted();
        jobQueueRepository.save(job);
    }

    private void markJobAsFailed(JobQueue job, String error) {
        job.markAsFailed();
        job.setError(error);
        jobQueueRepository.save(job);
    }

    private void sleepToPreventSpin() {
        try {
            Thread.sleep(POLL_INTERVAL_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}