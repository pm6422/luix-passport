package org.infinity.passport.lock.annotation;

import org.infinity.passport.config.ApplicationConstants;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLockAnnotation {

    String AROUND = "@annotation(" + ApplicationConstants.BASE_PACKAGE + ".lock.annotation.DistributedLockAnnotation)";

    /**
     * the maximum time to acquire the lock
     *
     * @return
     */
    long waitTime() default 1;

    /**
     * the maximum life time of cache
     *
     * @return
     */
    long keepAliveTime() default 30;

    /**
     * Time Unit
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;


    /**
     * Decide whether need to release lock after execution
     *
     * @return
     */
    boolean requireUnlock() default false;
}