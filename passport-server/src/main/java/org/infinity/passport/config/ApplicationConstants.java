package org.infinity.passport.config;

import org.infinity.passport.domain.HanlpPersonName;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

/**
 * Application constants.
 */
public final class ApplicationConstants {

    public static final String              BASE_PACKAGE                   = "org.infinity.passport";

    // Spring profile
    public static final String              SPRING_PROFILES_ACTIVE         = "spring.profiles.active";

    public static final String              SPRING_PROFILE_TEST            = "test";

    public static final String              SPRING_PROFILE_PRODUCTION      = "prod";

    // Spring profile used to disable swagger
    public static final String              SPRING_PROFILE_NO_SWAGGER      = "no-swagger";

    // Spring profile used to disable AOP logging
    public static final String              SPRING_PROFILE_NO_AOP_LOGGING  = "no-aop-logging";

    // Spring profile used to enable service metrics
    public static final String              SPRING_PROFILE_SERVICE_METRICS = "service-metrics";

    public static final String[]            AVAILABLE_PROFILES             = new String[] { SPRING_PROFILE_TEST,
            SPRING_PROFILE_PRODUCTION, SPRING_PROFILE_NO_SWAGGER, SPRING_PROFILE_NO_AOP_LOGGING,
            SPRING_PROFILE_SERVICE_METRICS };

    public static final String              SCHEDULE_LOG_PATTERN           = "########################Schedule executed: {}########################";

    public static final Locale              SYSTEM_LOCALE                  = Locale.SIMPLIFIED_CHINESE;

    public static final Map<String, String> HANLP_DICT_MAP                 = new HashMap<String, String>();

    static {
        HANLP_DICT_MAP.put(HanlpPersonName.class.getName(), HanlpPersonName.class.getSimpleName());
    }

}
