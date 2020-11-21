package org.infinity.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;

/**
 * Aspect for logging execution of Spring service components.
 */
@Aspect
@ConditionalOnProperty(prefix = "application.service-metrics", value = "enabled", havingValue = "true")
@Configuration
@Slf4j
public class ServiceMetricsAspectConfiguration {

    private static final String                SERVICE_PACKAGE = "within(" + ApplicationConstants.BASE_PACKAGE + ".service..*)";
    private final        ApplicationProperties applicationProperties;

    public ServiceMetricsAspectConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Around(SERVICE_PACKAGE)
    public Object logAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeMillis();
        if (elapsed > applicationProperties.getServiceMetrics().getSlowExecutionThreshold()) {
            log.warn("Found slow running method {}.{}() over {} ms",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), elapsed);
        }
        return result;
    }
}
