package com.luixtech.passport.aspect;

import lombok.AllArgsConstructor;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import com.luixtech.passport.config.ApplicationConstants;
import com.luixtech.passport.config.ApplicationProperties;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;

/**
 * Aspect for logging execution time of Spring components
 */
@Aspect
@ConditionalOnProperty("application.elapsed-time-logging.enabled")
@Configuration
@AllArgsConstructor
public class ElapsedTimeLoggingAspect {

    private static final String                SERVICE_PACKAGE = "within(" + ApplicationConstants.BASE_PACKAGE + ".service..*)";
    private static final String                HEADER_KEY      = "X-ELAPSED";
    private static final int                   SECOND          = 1000;
    private static final int                   MINUTE          = 60000;
    private final        ApplicationProperties applicationProperties;

    /**
     * Log method execution time of controller
     *
     * @param joinPoint join point
     * @return return value
     * @throws Throwable if any exception throws
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeMillis();

        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletResponse response = servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
        if (response != null) {
            // Store execution time to each http header
            if (elapsed < SECOND) {
                response.setHeader(HEADER_KEY, "" + elapsed + "ms");
            } else if (elapsed < MINUTE) {
                response.setHeader(HEADER_KEY, "" + elapsed / SECOND + "s");
            } else {
                response.setHeader(HEADER_KEY, "" + elapsed / (MINUTE) + "m");
            }
        }
        outputLog(joinPoint, elapsed);
        return result;
    }

    /**
     * Log method execution time of service
     *
     * @param joinPoint join point
     * @return return value
     * @throws Throwable if any exception throws
     */
    @Around(SERVICE_PACKAGE)
    public Object logService(ProceedingJoinPoint joinPoint) throws Throwable {
        StopWatch stopWatch = new StopWatch();
        stopWatch.start();
        Object result = joinPoint.proceed();
        stopWatch.stop();
        long elapsed = stopWatch.getTotalTimeMillis();
        outputLog(joinPoint, elapsed);
        return result;
    }

    private void outputLog(ProceedingJoinPoint joinPoint, long elapsed) {
        if (elapsed > applicationProperties.getElapsedTimeLogging().getSlowExecutionThreshold()) {
            if (elapsed < SECOND) {
                getLogger(joinPoint).warn("Found long running method {}() in {}ms",
                        joinPoint.getSignature().getName(), elapsed);
            } else if (elapsed < MINUTE) {
                getLogger(joinPoint).warn("Found long running method {}() in {}s",
                        joinPoint.getSignature().getName(), elapsed / 1000);
            } else {
                getLogger(joinPoint).warn("Found long running method {}() in {}m",
                        joinPoint.getSignature().getName(), elapsed / (1000 * 60));
            }
        }
    }

    /**
     * Retrieves the {@link Logger} associated to the given {@link JoinPoint}
     *
     * @param joinPoint join point we want the logger for
     * @return {@link Logger} associated to the given {@link JoinPoint}
     */
    private Logger getLogger(JoinPoint joinPoint) {
        return LoggerFactory.getLogger(joinPoint.getSignature().getDeclaringTypeName());
    }
}
