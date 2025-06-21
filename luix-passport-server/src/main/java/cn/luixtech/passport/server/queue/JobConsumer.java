package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.repository.JobQueueRepository;
import jakarta.annotation.PostConstruct;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
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
    private final    JobQueueRepository            jobQueueRepository;
    private final    Map<String, JobHandler>       pointToPointHandlers = new ConcurrentHashMap<>();
    private final    Map<String, BroadcastHandler> broadcastHandlers    = new ConcurrentHashMap<>();
    private final    ExecutorService               executorService;
    private volatile boolean                       running              = true;

    // 配置参数
    private static final int WORKER_THREADS   = 3;
    private static final int POLL_INTERVAL_MS = 1000;
    private static final int BATCH_SIZE       = 5;

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

    public void registerPointToPointHandler(String channel, JobHandler handler) {
        pointToPointHandlers.put(channel, handler);
        log.info("Registered point-to-point handler for channel: {}", channel);
    }

    public void registerBroadcastHandler(String channel, BroadcastHandler handler) {
        broadcastHandlers.put(channel, handler);
        log.info("Registered broadcast handler for channel: {}", channel);
    }

    @Transactional
    public void processJobs() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                if (processBatchJobs(BATCH_SIZE)) {
                    continue;
                }

                Optional<JobQueue> jobOpt = fetchAndProcessSingleJob();
                if (jobOpt.isEmpty()) {
                    Thread.sleep(POLL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Job processing error", e);
            }
        }
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public Optional<JobQueue> fetchAndProcessSingleJob() {
        return jobQueueRepository.lockAndGetNextPendingJob()
                .map(this::processJob);
    }

    @Transactional
    public boolean processBatchJobs(int batchSize) {
        List<JobQueue> jobs = jobQueueRepository.lockAndGetNextPendingJobs(batchSize);
        if (jobs.isEmpty()) {
            return false;
        }

        jobs.forEach(this::processJob);
        return true;
    }

    private JobQueue processJob(JobQueue job) {
        try {
            if (job.getBroadcastFlag()) {
                BroadcastHandler handler = broadcastHandlers.get(job.getChannel());
                if (handler != null) {
                    handler.handleBroadcast(job.getPayload());
                }
            } else {
                JobHandler handler = pointToPointHandlers.get(job.getChannel());
                if (handler != null) {
                    handler.handle(job);
                }
            }
            job.markAsCompleted();
        } catch (Exception e) {
            job.markAsFailed(e.getMessage());
            log.error("Failed to process job {}: {}", job.getId(), e.getMessage());
        }
        return jobQueueRepository.save(job);
    }
}