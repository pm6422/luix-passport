package org.infinity.passport.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

import org.infinity.passport.config.ApplicationConstants;

@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface ExecutionSwitch {

    public static final String AROUND = "@annotation(" + ApplicationConstants.BASE_PACKAGE
            + ".annotation.ExecutionSwitch)";

    /**
     * 
     * @return
     */
    String on();
}