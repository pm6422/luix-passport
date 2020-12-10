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
 * Aspect for logging elapsed time of Spring components.
 */
@Aspect
@ConditionalOnProperty(prefix = "application.elapsed-time-metrics", value = "enabled", havingValue = "true")
@Configuration
@Slf4j
public class ElapsedTimeAspect {

    private static final String                SERVICE_PACKAGE = "within(" + ApplicationConstants.BASE_PACKAGE + ".service..*)";
    private final        ApplicationProperties applicationProperties;

    public ElapsedTimeAspect(ApplicationProperties applicationProperties) {
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
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        return calculateExecutionTime(joinPoint, true);
    }

    @Around(SERVICE_PACKAGE)
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        return calculateExecutionTime(joinPoint, false);
    }

    private Object calculateExecutionTime(ProceedingJoinPoint joinPoint, boolean addResponseHeader) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeMillis();
        outputLog(joinPoint, elapsed);

        if (addResponseHeader) {
            addResponseHeader(elapsed);
        }
        return result;
    }

    private void outputLog(ProceedingJoinPoint joinPoint, long elapsed) {
        if (elapsed > applicationProperties.getElapsedTimeMetrics().getSlowExecutionThreshold()) {
            if (elapsed < 1000) {
                log.warn("Found slow running method {}.{}() over {}ms",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), elapsed);
            } else if (elapsed < 60000) {
                log.warn("Found slow running method {}.{}() over {}s",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), elapsed / 1000);
            } else {
                log.warn("Found slow running method {}.{}() over {}m",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), elapsed / 60000);
            }
        }
    }

    private void addResponseHeader(long elapsed) {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
        if (response != null) {
            // Store execution time to each http header
            if (elapsed < 1000) {
                response.setHeader("ELAPSED", "" + elapsed + "ms");
            } else if (elapsed < 60000) {
                response.setHeader("ELAPSED", "" + elapsed / 1000 + "s");
            } else {
                response.setHeader("ELAPSED", "" + elapsed / 60000 + "m");
            }
        }
    }
}
