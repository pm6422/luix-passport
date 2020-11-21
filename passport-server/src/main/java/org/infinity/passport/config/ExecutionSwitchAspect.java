package org.infinity.passport.config;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.infinity.passport.annotation.ExecutionSwitch;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;

import java.lang.reflect.Method;

import static org.springframework.core.annotation.AnnotationUtils.getAnnotation;

/**
 * Pointcut configuration
 */
@Aspect
@Configuration
public class ExecutionSwitchAspect {

    private final Environment env;

    public ExecutionSwitchAspect(Environment env) {
        this.env = env;
    }

    @Around(ExecutionSwitch.AROUND)
    public void switchAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method sourceMethod = joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(), proxyMethod.getParameterTypes());
        ExecutionSwitch sw = getAnnotation(sourceMethod, ExecutionSwitch.class);
        if (sw == null) {
            sw = getAnnotation(proxyMethod, ExecutionSwitch.class);
        }
        if (sw == null) {
            // Proceed to execute method
            joinPoint.proceed();
        } else {
            if ("true".equals(env.getProperty(sw.on()))) {
                // Proceed to execute method
                joinPoint.proceed();
            }
        }
    }
}
