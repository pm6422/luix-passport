package org.infinity.passport.config;

import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.infinity.passport.utils.RequestIdHolder;
import org.infinity.passport.utils.id.IdGenerator;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletResponse;
import java.util.Arrays;

/**
 * Aspect for logging execution arguments and result of the method.
 */
@Aspect
@ConditionalOnProperty(prefix = "application.aop-logging", value = "enabled", havingValue = "true")
@Configuration
@Slf4j
public class AopLoggingAspect {

    private final ApplicationProperties applicationProperties;

    public AopLoggingAspect(ApplicationProperties applicationProperties) {
        this.applicationProperties = applicationProperties;
    }

    /**
     * Log method arguments and result of controller
     * <p>
     * Refer to http://www.imooc.com/article/297283
     *
     * @param joinPoint join point
     * @return return value
     * @throws Throwable if exception occurs
     */
    @Around("execution(@(org.springframework.web.bind.annotation.*Mapping) * *(..))")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        if (log.isInfoEnabled() && inLoggingMethods(joinPoint)) {
            RequestIdHolder.setRequestId(IdGenerator.generateRequestId());
            log.info("Request: {}.{}() with requestId = {} and argument[s] = {}",
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName(),
                    RequestIdHolder.getRequestId(),
                    Arrays.toString(joinPoint.getArgs()));
        }
        try {
            Object result = joinPoint.proceed();
            if (log.isInfoEnabled() && inLoggingMethods(joinPoint)) {
                HttpServletResponse response = getHttpServletResponse();
                if (response != null) {
                    response.setHeader("X-REQUEST-ID", RequestIdHolder.getRequestId());
                }
                log.info("Response: {}.{}() with requestId = {} and result = {}",
                        joinPoint.getSignature().getDeclaringType().getSimpleName(),
                        joinPoint.getSignature().getName(),
                        RequestIdHolder.getRequestId(),
                        result);
            }
            return result;
        } catch (IllegalArgumentException e) {
            // Catch illegal argument exception and re-throw
            log.error("Illegal argument[s]: {} in {}.{}()",
                    Arrays.toString(joinPoint.getArgs()),
                    joinPoint.getSignature().getDeclaringType().getSimpleName(),
                    joinPoint.getSignature().getName());
            throw e;
        }
    }

    private boolean inLoggingMethods(ProceedingJoinPoint joinPoint) {
        String method = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." +
                joinPoint.getSignature().getName();
        return applicationProperties.getAopLogging().getLoggingMethods().contains(method);
    }

    private HttpServletResponse getHttpServletResponse() {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        return servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
    }
}