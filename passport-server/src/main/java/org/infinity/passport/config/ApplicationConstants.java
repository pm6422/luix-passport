package org.infinity.passport.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Locale;

/**
 * Application constants.
 */
public final class ApplicationConstants {

    private static final Logger   LOGGER                         = LoggerFactory.getLogger(ApplicationConstants.class);
    public static final  String   BASE_PACKAGE                   = "org.infinity.passport";
    // Spring profile
    public static final  String   SPRING_PROFILES_ACTIVE         = "spring.profiles.active";
    public static final  String   SPRING_PROFILE_TEST            = "test";
    public static final  String   SPRING_PROFILE_PRODUCTION      = "prod";
    // Spring profile used to disable swagger
    public static final  String   SPRING_PROFILE_NO_SWAGGER      = "no-swagger";
    // Spring profile used to disable AOP logging
    public static final  String   SPRING_PROFILE_NO_AOP_LOGGING  = "no-aop-logging";
    // Spring profile used to enable service metrics
    public static final  String   SPRING_PROFILE_SERVICE_METRICS = "service-metrics";
    public static final  String[] AVAILABLE_PROFILES             = new String[]{SPRING_PROFILE_TEST,
            SPRING_PROFILE_PRODUCTION, SPRING_PROFILE_NO_SWAGGER, SPRING_PROFILE_NO_AOP_LOGGING,
            SPRING_PROFILE_SERVICE_METRICS};
    public static final  String   SCHEDULE_LOG_PATTERN           = "########################Schedule executed: {}########################";
    public static final  Locale   SYSTEM_LOCALE                  = Locale.SIMPLIFIED_CHINESE;
}
