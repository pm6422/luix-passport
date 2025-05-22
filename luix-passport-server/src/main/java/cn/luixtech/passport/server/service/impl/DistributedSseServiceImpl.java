//package cn.luixtech.passport.server.service.impl;
//
//import cn.luixtech.passport.server.service.SseService;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Service;
//import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;
//
//import java.io.IOException;
//import java.util.Map;
//import java.util.Set;
//import java.util.concurrent.ConcurrentHashMap;
//import java.util.concurrent.Executors;
//import java.util.concurrent.ScheduledExecutorService;
//import java.util.concurrent.TimeUnit;
//import java.util.function.Consumer;
//
//@Service
//@Slf4j
//public class DistributedSseServiceImpl implements SseService {
//    private static final Map<String, SseEmitter>  LOCAL_USER_EMITTERS        = new ConcurrentHashMap<>();
//    private static final ScheduledExecutorService SCHEDULED_EXECUTOR_SERVICE = Executors.newScheduledThreadPool(1);
//
//    private final RabbitTemplate rabbitTemplate;
//
//    public DistributedSseServiceImpl() {
//        // clean up expired sse emitters every 5 minutes
//        SCHEDULED_EXECUTOR_SERVICE.scheduleAtFixedRate(this::cleanUp, 5, 5, TimeUnit.MINUTES);
//    }
//
//    @Override
//    public SseEmitter add(String userId) {
//        if (LOCAL_USER_EMITTERS.containsKey(userId)) {
//            return LOCAL_USER_EMITTERS.get(userId);
//        }
//        try {
//            // Set the timeout period to 30 minutes
//            SseEmitter sseEmitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
//            sseEmitter.onCompletion(completionCallback(userId));
//            sseEmitter.onError(errorCallback(userId));
//            sseEmitter.onTimeout(timeoutCallback(userId));
//            LOCAL_USER_EMITTERS.put(userId, sseEmitter);
//            return sseEmitter;
//        } catch (Exception e) {
//            log.error("Failed to create SseEmitter connection for user ID: " + userId, e);
//            return null;
//        }
//    }
//
//    @Override
//    public void remove(String userId) {
//        LOCAL_USER_EMITTERS.remove(userId);
//    }
//
//    @Override
//    public void removeAll() {
//        LOCAL_USER_EMITTERS.clear();
//    }
//
//    @RabbitListener(queues = "sse.${spring.application.instance-id}")
//    public void handleRemoteSseMessage(SseMessage message) {
//        if (LOCAL_USER_EMITTERS.containsKey(message.getUserId())) {
//            pushMessage(message.getUserId(), message.getMessage());
//        }
//    }
//
//    @Override
//    public void pushMessage(String userId, String message) {
//        if (LOCAL_USER_EMITTERS.containsKey(userId)) {
//            SseEmitter emitter = LOCAL_USER_EMITTERS.get(userId);
//            try {
//                emitter.send(message);
//            } catch (IOException e) {
//                remove(userId);
//            }
//        } else {
//            // Broadcast to all users
//            rabbitTemplate.convertAndSend("sse.exchange", "user." + userId, SseMessage.of(userId, message));
//        }
//    }
//
//    @Override
//    public void cleanUp() {
//        LOCAL_USER_EMITTERS.entrySet().removeIf(entry -> {
//            SseEmitter emitter = entry.getValue();
//            return emitter == null || isEmitterDead(emitter);
//        });
//    }
//
//    @Override
//    public Set<String> getOnlineUserIds() {
//        return LOCAL_USER_EMITTERS.keySet();
//    }
//
//    /**
//     * Process completion callback
//     *
//     * @param userId user ID
//     * @return an Runnable
//     */
//    private Runnable completionCallback(String userId) {
//        return () -> {
//            log.info("Completed SEE with user ID: {}", userId);
//            remove(userId);
//        };
//    }
//
//    /**
//     * Process timeout callback
//     *
//     * @param userId user ID
//     * @return an Runnable
//     */
//    private Runnable timeoutCallback(String userId) {
//        return () -> {
//            log.info("SEE timeout with user ID: {}", userId);
//            remove(userId);
//        };
//    }
//
//    /**
//     * Process error callback
//     *
//     * @param userId user ID
//     * @return an Runnable
//     */
//    private Consumer<Throwable> errorCallback(String userId) {
//        return throwable -> {
//            log.info(" SEE error with user ID: " + userId, throwable);
//            remove(userId);
//        };
//    }
//
//    private boolean isEmitterDead(SseEmitter emitter) {
//        try {
//            emitter.send(SseEmitter.event().comment("ping"));
//            return false;
//        } catch (Exception e) {
//            return true;
//        }
//    }
//}
