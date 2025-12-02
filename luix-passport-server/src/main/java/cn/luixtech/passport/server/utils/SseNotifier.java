package cn.luixtech.passport.server.utils;

import java.lang.reflect.Method;

public final class SseNotifier {
    private static final String UTIL_CLASS = "com.luixtech.springbootframework.utils.SseEmitterUtils";

    private SseNotifier() {}

    public static void notifyUser(String username, String message) {
        try {
            Class<?> clazz = Class.forName(UTIL_CLASS);
            Method m = findMethod(clazz);
            if (m != null) {
                if (m.getParameterCount() == 2 && m.getParameterTypes()[1] == Object.class) {
                    m.invoke(null, username, (Object) message);
                } else {
                    m.invoke(null, username, message);
                }
            }
        } catch (Throwable ignored) {
        }
    }

    private static Method findMethod(Class<?> clazz) {
        String[] candidates = new String[]{
            "pushUserMessage", "pushMessage", "notifyUser", "sendToUser"
        };
        for (String name : candidates) {
            try {
                return clazz.getMethod(name, String.class, String.class);
            } catch (NoSuchMethodException e) {
                try {
                    return clazz.getMethod(name, String.class, Object.class);
                } catch (NoSuchMethodException ignored) {
                }
            }
        }
        return null;
    }
}
