package org.infinity.passport.config;

import java.util.Arrays;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.annotation.Pointcut;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.Configuration;
import org.springframework.context.annotation.Profile;
import org.springframework.util.StopWatch;

/**
 * Aspect for logging execution of service Spring components.
 */
@Aspect
@Profile(ApplicationConstants.SPRING_PROFILE_SERVICE_METRICS)
@Configuration
public class ServiceMetricsPointCutConfiguration {

    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceMetricsPointCutConfiguration.class);

    @Pointcut("within(" + ApplicationConstants.BASE_PACKAGE + ".service.impl.*)")
    public void servicePointcut() {
    }

    @Around("servicePointcut()")
    public Object serviceAround(ProceedingJoinPoint joinPoint) throws Throwable {
        try {
            StopWatch stopWatch = new StopWatch();
            stopWatch.start();
            Object result = joinPoint.proceed();
            stopWatch.stop();
            long elapsed = stopWatch.getTotalTimeMillis();
            long threshold = 10L;
            if (elapsed > threshold) {
                LOGGER.warn("@@@@@@@@@@@@@@@@@@@@@ Exit: {}.{}() with {} ms @@@@@@@@@@@@@@@@@@@@@",
                        joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName(), elapsed);
            }
            return result;
        } catch (IllegalArgumentException e) {
            LOGGER.error("Illegal argument: {} in {}.{}()", Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringTypeName(), joinPoint.getSignature().getName());

            throw e;
        }
    }
}
