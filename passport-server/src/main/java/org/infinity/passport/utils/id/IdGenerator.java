package org.infinity.passport.utils.id;

import javax.annotation.concurrent.ThreadSafe;

@ThreadSafe
public abstract class IdGenerator {
    private static final ShortIdGenerator SHORT_ID_GENERATOR = new ShortIdGenerator();

    /**
     * Generate a thread-safe digit format ID
     *
     * @return 19 bits length，e.g：1672888135850179037
     */
    public static long generateTimestampId() {
        return TimestampIdGenerator.nextId();
    }

    /**
     * Generate a thread-safe digit format ID, it can be used in a low concurrency environment
     *
     * @return 12 bits length，e.g：306554419571
     */
    public static long generateShortId() {
        return SHORT_ID_GENERATOR.nextId();
    }
}
