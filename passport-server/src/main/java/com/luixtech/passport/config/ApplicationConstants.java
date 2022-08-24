package com.luixtech.passport.config;

import java.util.Locale;

/**
 * Application constants.
 */
public final class ApplicationConstants {

    public static final String   BASE_PACKAGE        = "com.luixtech.passport";
    public static final String   SPRING_PROFILE_TEST = "test";
    public static final String   SPRING_PROFILE_PROD = "prod";
    public static final String[] AVAILABLE_PROFILES  = new String[]{SPRING_PROFILE_TEST, SPRING_PROFILE_PROD};
    public static final Locale   SYSTEM_LOCALE       = Locale.SIMPLIFIED_CHINESE;
}
