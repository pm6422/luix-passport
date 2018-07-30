package org.infinity.passport.config;

import java.lang.reflect.Method;

import org.aspectj.lang.ProceedingJoinPoint;
import org.aspectj.lang.annotation.Around;
import org.aspectj.lang.annotation.Aspect;
import org.aspectj.lang.reflect.MethodSignature;
import org.infinity.passport.annotation.ExecutionSwitch;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.AnnotationUtils;
import org.springframework.core.env.Environment;

/**
 * Pointcut configuration
 */
@Aspect
@Configuration
public class ExecutionSwitchPointCutConfiguration {

    @Autowired
    private Environment env;

    @Around(ExecutionSwitch.AROUND)
    public void switchAround(ProceedingJoinPoint joinPoint) throws Throwable {
        Method proxyMethod = ((MethodSignature) joinPoint.getSignature()).getMethod();
        Method soruceMethod = joinPoint.getTarget().getClass().getMethod(proxyMethod.getName(),
                proxyMethod.getParameterTypes());
        ExecutionSwitch sw = AnnotationUtils.getAnnotation(soruceMethod, ExecutionSwitch.class);
        if (sw == null) {
            sw = AnnotationUtils.getAnnotation(proxyMethod, ExecutionSwitch.class);
        }
        if (sw == null) {
            // Proceed to execute method
            joinPoint.proceed();
        } else {
            if (env.getProperty(sw.on()).equals("true")) {
                // Proceed to execute method
                joinPoint.proceed();
            }
        }
    }
}
