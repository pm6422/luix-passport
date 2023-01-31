package com.luixtech.passport.config;

import com.luixtech.passport.async.ExceptionHandlingAsyncTaskExecutor;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.aop.interceptor.AsyncUncaughtExceptionHandler;
import org.springframework.aop.interceptor.SimpleAsyncUncaughtExceptionHandler;
import org.springframework.boot.autoconfigure.task.TaskExecutionProperties;
import org.springframework.boot.autoconfigure.task.TaskSchedulingProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.task.AsyncTaskExecutor;
import org.springframework.scheduling.annotation.AsyncConfigurer;
import org.springframework.scheduling.annotation.EnableAsync;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.concurrent.ThreadPoolTaskExecutor;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;
import org.springframework.web.servlet.config.annotation.AsyncSupportConfigurer;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

import java.util.concurrent.Executor;

@Configuration
@EnableAsync
@EnableScheduling
@AllArgsConstructor
@Slf4j
public class AsyncConfiguration implements AsyncConfigurer, WebMvcConfigurer {
    private final TaskExecutionProperties  taskExecutionProperties;
    private final TaskSchedulingProperties taskSchedulingProperties;

    @Override
    @Bean(name = "asyncTaskExecutor")
    public Executor getAsyncExecutor() {
        ThreadPoolTaskExecutor executor = new ThreadPoolTaskExecutor();
        executor.setCorePoolSize(taskExecutionProperties.getPool().getCoreSize());
        executor.setMaxPoolSize(taskExecutionProperties.getPool().getMaxSize());
        executor.setQueueCapacity(taskExecutionProperties.getPool().getQueueCapacity());
        executor.setThreadNamePrefix(taskExecutionProperties.getThreadNamePrefix());
        log.info("Created async task executor with corePoolSize: [{}], maxPoolSize: [{}] and queueCapacity: [{}]",
                executor.getCorePoolSize(), executor.getMaxPoolSize(), taskExecutionProperties.getPool().getQueueCapacity());
        return new ExceptionHandlingAsyncTaskExecutor(executor);
    }

    @Override
    public AsyncUncaughtExceptionHandler getAsyncUncaughtExceptionHandler() {
        return new SimpleAsyncUncaughtExceptionHandler();
    }

    /**
     * 暂时搞不清楚作用
     *
     * @return
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        ThreadPoolTaskScheduler taskScheduler = new ThreadPoolTaskScheduler();
        taskScheduler.setPoolSize(taskSchedulingProperties.getPool().getSize());
        taskScheduler.setRemoveOnCancelPolicy(true);
        taskScheduler.setErrorHandler(t -> log.error("Unexpected error occurred while executing scheduled task!", t));
        taskScheduler.setThreadNamePrefix(taskSchedulingProperties.getThreadNamePrefix());
        return taskScheduler;
    }

    @Override
    public void configureAsyncSupport(AsyncSupportConfigurer configurer) {
        configurer.setTaskExecutor((AsyncTaskExecutor) getAsyncExecutor());
    }
}
