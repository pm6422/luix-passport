package org.infinity.passport.controller.advice;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.infinity.passport.config.ApplicationProperties;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.util.StopWatch;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

import static org.infinity.passport.config.ApplicationConstants.CONTROLLER_PACKAGE;

/**
 * Aspect for logging execution of Spring components.
 */
@Aspect
@ConditionalOnProperty(prefix = "application.service-metrics", value = "enabled", havingValue = "true")
@Configuration
@Slf4j
public class ControllerMetricsAspect {

    private final ApplicationProperties applicationProperties;

    public ControllerMetricsAspect(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    @Around("within(" + CONTROLLER_PACKAGE + "*)")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
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
                log.warn("Slowly executed {}.{}() in {} ms",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(), joinPoint.getSignature().getName(), elapsed);
            }
            return result;
        } catch (IllegalArgumentException e) {
            log.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());
            throw e;
        }
    }
}
