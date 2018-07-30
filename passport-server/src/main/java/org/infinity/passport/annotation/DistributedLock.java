package org.infinity.passport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;
import java.util.concurrent.TimeUnit;

import org.infinity.passport.config.ApplicationConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface DistributedLock {

    public static final String AROUND = "@annotation(" + ApplicationConstants.BASE_PACKAGE
            + ".annotation.DistributedLock)";

    /**
     * Expiration
     *
     * @return
     */
    int expiration() default 30;

    /**
     * Time Unit
     *
     * @return
     */
    TimeUnit timeUnit() default TimeUnit.SECONDS;
}