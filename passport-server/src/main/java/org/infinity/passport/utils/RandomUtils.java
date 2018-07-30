package org.infinity.passport.utils;

import java.security.SecureRandom;

import org.apache.commons.lang3.RandomStringUtils;

public final class RandomUtils {

    private static final int LENGTH = 20;

    private RandomUtils() {
    }

    /**
     * Generates a ID.
     * 非线程安全
     * 测试用例写法：利用数据库主键唯一特性，for循环生成一万条数据插到数据库中，如果不报错则安全
     *
     * @return the generated ID
     */
    public static String generateId() {
        return "" + new IdWorker(new SecureRandom().nextInt(30), new SecureRandom().nextInt(10)).nextId();
    }

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
