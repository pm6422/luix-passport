package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.service.SseService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.io.IOException;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

@Service
@Slf4j
public class LocalSseServiceImpl implements SseService {
    private static final Map<String, SseEmitter>  USER_EMITTER_CACHE         = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);

    public LocalSseServiceImpl() {
        // clean up expired sse emitters every 5 minutes
        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::cleanUp, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public SseEmitter add(String userId) {
        if (USER_EMITTER_CACHE.containsKey(userId)) {
            return USER_EMITTER_CACHE.get(userId);
        }
        try {
            // Set the timeout period to 30 minutes
            SseEmitter sseEmitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
            sseEmitter.onCompletion(completionCallback(userId));
            sseEmitter.onError(errorCallback(userId));
            sseEmitter.onTimeout(timeoutCallback(userId));
            USER_EMITTER_CACHE.put(userId, sseEmitter);
            return sseEmitter;
        } catch (Exception e) {
            log.error("Failed to create SseEmitter connection for user ID: " + userId, e);
            return null;
        }
    }

    @Override
    public void remove(String userId) {
        USER_EMITTER_CACHE.remove(userId);
    }

    @Override
    public void removeAll() {
        USER_EMITTER_CACHE.clear();
    }

    @Override
    public void pushMessage(String userId, String message) {
        if (!USER_EMITTER_CACHE.containsKey(userId)) {
            return;
        }
        try {
            USER_EMITTER_CACHE.get(userId).send(message);
        } catch (IOException e) {
            log.error("Failed to push message to user: " + userId, e);
            remove(userId);
        }
    }

    @Override
    public void cleanUp() {
        USER_EMITTER_CACHE.entrySet().removeIf(entry -> {
            SseEmitter emitter = entry.getValue();
            return emitter == null || isEmitterDead(emitter);
        });
    }

    @Override
    public Set<String> getOnlineUserIds() {
        return USER_EMITTER_CACHE.keySet();
    }

    /**
     * Process completion callback
     *
     * @param userId user ID
     * @return an Runnable
     */
    private Runnable completionCallback(String userId) {
        return () -> {
            log.info("Completed SEE with user ID: {}", userId);
            remove(userId);
        };
    }

    /**
     * Process timeout callback
     *
     * @param userId user ID
     * @return an Runnable
     */
    private Runnable timeoutCallback(String userId) {
        return () -> {
            log.info("SEE timeout with user ID: {}", userId);
            remove(userId);
        };
    }

    /**
     * Process error callback
     *
     * @param userId user ID
     * @return an Runnable
     */
    private Consumer<Throwable> errorCallback(String userId) {
        return throwable -> {
            log.info(" SEE error with user ID: " + userId, throwable);
            remove(userId);
        };
    }

    private boolean isEmitterDead(SseEmitter emitter) {
        try {
            emitter.send(SseEmitter.event().comment("ping"));
            return false;
        } catch (Exception e) {
            return true;
        }
    }
}
