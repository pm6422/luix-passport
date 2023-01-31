package com.luixtech.passport.aspect;

import com.alibaba.fastjson2.JSON;
import com.luixtech.passport.config.ApplicationProperties;
import com.luixtech.passport.utils.TraceIdUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.aspectj.lang.JoinPoint;
import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.AfterThrowing;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.util.HashMap;
import java.util.Map;

/**
 * Aspect for logging execution arguments and result of the method.
 */
@Aspect
@ConditionalOnProperty("application.aop-logging.enabled")
@Configuration
@AllArgsConstructor
@Slf4j
public class AopLoggingAspect {
    private static final String                UNKNOWN_ARG_VAL = "UNKNOWN";
    private final        ApplicationProperties applicationProperties;

    /**
     * Advice that logs methods throwing exceptions
     *
     * @param joinPoint join point for advice
     * @param e         exception
     */
    @AfterThrowing(pointcut = "within(@org.springframework.web.bind.annotation.RestController *)", throwing = "e")
    public void logAfterThrowing(JoinPoint joinPoint, Throwable e) {
        getLogger(joinPoint).error(
                "Exception in {}() with cause = '{}' and exception = '{}'",
                joinPoint.getSignature().getName(),
                e.getCause() != null ? e.getCause() : "NULL",
                e.getMessage());
    }

    /**
     * Log method arguments and result of controller
     *
     * @param joinPoint join point
     * @return return value
     * @throws Throwable if exception occurs
     */
    @Around("within(@org.springframework.web.bind.annotation.RestController *)")
    public Object logController(ProceedingJoinPoint joinPoint) throws Throwable {
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes != null ? servletRequestAttributes.getRequest() : null;
        HttpServletResponse response = servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
        // Get traceId from http request
        TraceIdUtils.setTraceId(request);
        beforeRun(joinPoint);
        Object result = joinPoint.proceed();
        // Set traceId to http response
        TraceIdUtils.setTraceId(response);
        afterRun(joinPoint, result);
        TraceIdUtils.remove();
        return result;
    }

    public void beforeRun(ProceedingJoinPoint joinPoint) {
        if (enablePrint(joinPoint)) {
            return;
        }
        String[] paramNames = ((MethodSignature) joinPoint.getSignature()).getParameterNames();
        Object[] arguments = joinPoint.getArgs();
        Map<String, Object> validParamMap = new HashMap<>(arguments.length);
        for (int i = 0; i < arguments.length; i++) {
            String argVal = UNKNOWN_ARG_VAL;
            if (isValidArgument(arguments[i])) {
                try {
                    argVal = JSON.toJSONString(arguments[i]);
                } catch (Exception ex) {
                    log.warn("Failed to serialize argument!");
                }
            }
            validParamMap.put(paramNames[i], argVal);
        }
        getLogger(joinPoint).info("Enter method {}() with argument[s] = {}",
                joinPoint.getSignature().getName(), validParamMap);
    }

    private boolean isValidArgument(Object argument) {
        return !(argument instanceof ServletRequest)
                && !(argument instanceof ServletResponse)
                && !(argument instanceof MultipartFile);
    }

    private void afterRun(ProceedingJoinPoint joinPoint, Object result) {
        if (enablePrint(joinPoint)) {
            return;
        }
        getLogger(joinPoint).info("Exit method {}() with result = {}", joinPoint.getSignature().getName(), result);
    }

    private boolean enablePrint(ProceedingJoinPoint joinPoint) {
        return !getLogger(joinPoint).isInfoEnabled() || !matchLogMethod(joinPoint);
    }

    private boolean matchLogMethod(ProceedingJoinPoint joinPoint) {
        if (!applicationProperties.getAopLogging().isMethodWhitelistMode()) {
            return true;
        }
        String method = joinPoint.getSignature().getDeclaringType().getSimpleName() + "." +
                joinPoint.getSignature().getName();
        return applicationProperties.getAopLogging().getMethodWhitelist().contains(method);
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