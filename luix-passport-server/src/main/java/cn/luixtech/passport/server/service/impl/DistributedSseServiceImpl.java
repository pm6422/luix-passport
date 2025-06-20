package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.pojo.SseMessage;
import cn.luixtech.passport.server.queue.JobConsumer;
import cn.luixtech.passport.server.queue.JobHandler;
import cn.luixtech.passport.server.service.SseService;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class DistributedSseServiceImpl implements SseService, JobHandler {
    private static final String                   SSE_JOB_TYPE        = "sse_operation";
    private static final Map<String, SseEmitter>  LOCAL_USER_EMITTERS = new ConcurrentHashMap<>();
    private static final ScheduledExecutorService CLEANUP_EXECUTOR    = Executors.newSingleThreadScheduledExecutor();

    @Autowired
    public DistributedSseServiceImpl(JobConsumer jobConsumer) {
        // 注册为SSE作业处理器
        jobConsumer.registerHandler(this);

        // 每5分钟清理一次过期的SSE连接
        CLEANUP_EXECUTOR.scheduleAtFixedRate(this::cleanUp, 5, 5, TimeUnit.MINUTES);
    }

    @Override
    public String getJobType() {
        return SSE_JOB_TYPE;
    }

    @Override
    public void handle(JobQueue job) {
        SseMessage message = parsePayload(job.getPayload());
        if (message != null) {
            pushMessage(message.getUsername(), message.getMessage());
        }
    }

    @Override
    public SseEmitter add(String username) {
        if (LOCAL_USER_EMITTERS.containsKey(username)) {
            return LOCAL_USER_EMITTERS.get(username);
        }
        try {
            // Set the timeout period to 30 minutes
            SseEmitter sseEmitter = new SseEmitter(TimeUnit.MINUTES.toMillis(30));
            sseEmitter.onCompletion(completionCallback(username));
            sseEmitter.onError(errorCallback(username));
            sseEmitter.onTimeout(timeoutCallback(username));
            LOCAL_USER_EMITTERS.put(username, sseEmitter);
            return sseEmitter;
        } catch (Exception e) {
            log.error("Failed to create SseEmitter connection for user ID: " + username, e);
            return null;
        }
    }

    @Override
    public void remove(String username) {
        LOCAL_USER_EMITTERS.remove(username);
    }

    @Override
    public void removeAll() {
        LOCAL_USER_EMITTERS.clear();
    }

    @Override
    public void pushMessage(String username, String message) {
        if (!LOCAL_USER_EMITTERS.containsKey(username)) {
            return;
        }
        try {
            LOCAL_USER_EMITTERS.get(username).send(message);
        } catch (IOException e) {
            log.error("Failed to push message to user: " + username, e);
            remove(username);
        }
    }

    @Override
    public void cleanUp() {
        LOCAL_USER_EMITTERS.entrySet().removeIf(entry -> {
            SseEmitter emitter = entry.getValue();
            return emitter == null || isEmitterDead(emitter);
        });
    }

    @Override
    public Set<String> getOnlineUsernames() {
        return LOCAL_USER_EMITTERS.keySet();
    }

    /**
     * Process completion callback
     *
     * @param username user ID
     * @return an Runnable
     */
    private Runnable completionCallback(String username) {
        return () -> {
            log.info("Completed SEE with user ID: {}", username);
            remove(username);
        };
    }

    /**
     * Process timeout callback
     *
     * @param username user ID
     * @return an Runnable
     */
    private Runnable timeoutCallback(String username) {
        return () -> {
            log.info("SEE timeout with user ID: {}", username);
            remove(username);
        };
    }

    /**
     * Process error callback
     *
     * @param username user ID
     * @return an Runnable
     */
    private Consumer<Throwable> errorCallback(String username) {
        return throwable -> {
            log.info(" SEE error with user ID: " + username, throwable);
            remove(username);
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

    private SseMessage parsePayload(String payload) {
        try {
            return new ObjectMapper().readValue(payload, SseMessage.class);
        } catch (JsonProcessingException e) {
            log.error("Failed to parse payload", e);
            return null;
        }
    }
}