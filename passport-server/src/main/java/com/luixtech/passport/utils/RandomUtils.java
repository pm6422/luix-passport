package com.luixtech.passport.utils;

import org.apache.commons.lang3.RandomStringUtils;

public abstract class RandomUtils {

    private static final int LENGTH = 20;

    /**
     * Generates a password.
     *
     * @return the generated password
     */
    public static String generatePassword() {
        return RandomStringUtils.randomAlphanumeric(LENGTH);
    }

    /**
     * Generates an activation key.
     *
     * @return the generated activation key
     */
    public static String generateActivationKey() {
        return RandomStringUtils.randomNumeric(LENGTH);
    }

    /**
     * Generates a reset key.
     *
     * @return the generated reset key
     */
    public static String generateResetKey() {
        return RandomStringUtils.randomNumeric(LENGTH);
    }
}
