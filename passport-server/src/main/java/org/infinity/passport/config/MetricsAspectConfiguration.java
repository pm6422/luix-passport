package org.infinity.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * Aspect for logging execution of Spring components.
 */
@Aspect
@ConditionalOnProperty(prefix = "application.service-metrics", value = "enabled", havingValue = "true")
@Configuration
@Slf4j
public class MetricsAspectConfiguration {

    private static final String                SERVICE_PACKAGE = "within(" + ApplicationConstants.BASE_PACKAGE + ".service..*)";
    private final        ApplicationProperties applicationProperties;

    public MetricsAspectConfiguration(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * Refer to http://www.imooc.com/article/297283
     *
     * @param joinPoint join point
     * @return return value
     * @throws Throwable if exception occurs
     */
    @Around("execution(@(org.springframework.web.bind.annotation.*Mapping) * *(..))")
    public Object controllerAround(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeMillis();

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
        if (response != null) {
            // Store execution time to each http header
            response.setHeader("ELAPSED", "" + elapsed + "ms");
        }
        if (elapsed > applicationProperties.getServiceMetrics().getSlowExecutionThreshold()) {
            log.warn("Found slow running method {}.{}() over {} ms",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), elapsed);
        }
        return result;
    }

    @Around(SERVICE_PACKAGE)
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
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
