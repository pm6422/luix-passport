package cn.luixtech.passport.server.component.jobqueue;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.repository.JobQueueRepository;
import jakarta.annotation.PreDestroy;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.transaction.support.TransactionTemplate;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.ThreadFactory;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;

@Component
@Slf4j
public class JobConsumer {
    private final JobQueueRepository jobQueueRepository;
    private final Map<String, JobConsumerHandler> pointToPointHandlers = new ConcurrentHashMap<>();
    private final ExecutorService executorService;
    private final TransactionTemplate transactionTemplate;
    private volatile boolean running = true;

    // Configuration parameters
    private static final int WORKER_THREADS = 3;
    private static final int POLL_INTERVAL_MS = 1000;
    private static final int BATCH_SIZE = 5;

    public JobConsumer(JobQueueRepository jobQueueRepository,
            PlatformTransactionManager transactionManager,
            List<JobConsumerHandler> handlers) {
        this.jobQueueRepository = jobQueueRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);

        // Automatically register all handlers
        for (JobConsumerHandler handler : handlers) {
            pointToPointHandlers.put(handler.getChannel(), handler);
            log.info("Auto-registered handler for channel: {}", handler.getChannel());
        }

        // Use meaningful thread names for easier debugging
        this.executorService = Executors.newFixedThreadPool(WORKER_THREADS, new ThreadFactory() {
            private final AtomicInteger count = new AtomicInteger(1);

            @Override
            public Thread newThread(Runnable r) {
                return new Thread(r, "job-consumer-" + count.getAndIncrement());
            }
        });
    }

    @EventListener(ApplicationReadyEvent.class)
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
            if (!executorService.awaitTermination(5, TimeUnit.SECONDS)) {
                executorService.shutdownNow();
            }
        } catch (InterruptedException e) {
            executorService.shutdownNow();
            Thread.currentThread().interrupt();
        }
        log.info("Job consumer stopped");
    }

    public void processJobs() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // Manually manage transactions using TransactionTemplate to avoid
                // @Transactional self-invocation issues
                Boolean processed = transactionTemplate.execute(status -> {
                    if (processBatchJobs(BATCH_SIZE)) {
                        return true;
                    }
                    return fetchAndProcessSingleJob().isPresent();
                });

                if (Boolean.FALSE.equals(processed)) {
                    TimeUnit.MILLISECONDS.sleep(POLL_INTERVAL_MS);
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            } catch (Exception e) {
                log.error("Job processing error", e);
                // Sleep briefly on exception to avoid log flooding in tight loops
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // Note: This method is now called within TransactionTemplate, so @Transactional
    // annotation is not needed.
    // If finer-grained transaction control (e.g., independent transaction for
    // processJob) is required, logic adjustment is needed.
    public Optional<JobQueue> fetchAndProcessSingleJob() {
        return jobQueueRepository.lockAndGetNextPendingJob().map(this::processJob);
    }

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
            JobConsumerHandler handler = pointToPointHandlers.get(job.getChannel());
            if (handler != null) {
                handler.handle(job);
            } else {
                log.warn("No handler found for channel: {}", job.getChannel());
            }
            job.markAsCompleted();
        } catch (Exception e) {
            job.markAsFailed(e.getMessage());
            log.error("Failed to process job {}: {}", job.getId(), e.getMessage());
        }
        return jobQueueRepository.save(job);
    }
}
