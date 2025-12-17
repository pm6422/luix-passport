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

    // 配置参数
    private static final int WORKER_THREADS = 3;
    private static final int POLL_INTERVAL_MS = 1000;
    private static final int BATCH_SIZE = 5;

    public JobConsumer(JobQueueRepository jobQueueRepository, PlatformTransactionManager transactionManager) {
        this.jobQueueRepository = jobQueueRepository;
        this.transactionTemplate = new TransactionTemplate(transactionManager);
        // 使用有意义的线程名称，便于调试
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

    public void registerPointToPointHandler(String channel, JobConsumerHandler handler) {
        pointToPointHandlers.put(channel, handler);
        log.info("Registered point-to-point handler for channel: {}", channel);
    }

    public void processJobs() {
        while (running && !Thread.currentThread().isInterrupted()) {
            try {
                // 使用 TransactionTemplate 手动管理事务，避免 @Transactional 自调用失效的问题
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
                // 发生异常时稍作休眠，避免死循环打印日志
                try {
                    TimeUnit.MILLISECONDS.sleep(1000);
                } catch (InterruptedException ex) {
                    Thread.currentThread().interrupt();
                }
            }
        }
    }

    // 注意：此方法现在在 TransactionTemplate 中调用，所以不需要 @Transactional 注解
    // 如果需要更细粒度的事务控制（如 processJob 独立事务），需要调整逻辑
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
