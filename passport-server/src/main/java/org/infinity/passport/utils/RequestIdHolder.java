package org.infinity.passport.utils;

public abstract class RequestIdHolder {
    private static final ThreadLocal<String> HOLDER = ThreadLocal.withInitial(() -> "");

    public static String getRequestId() {
        return HOLDER.get();
    }

    public static void setRequestId(String requestId) {
        HOLDER.set(requestId);
    }

    public static void destroy() {
        HOLDER.remove();
    }
}
