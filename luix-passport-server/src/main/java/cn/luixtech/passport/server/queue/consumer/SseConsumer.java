package cn.luixtech.passport.server.queue.consumer;

import cn.luixtech.passport.server.pojo.SseMessage;
import cn.luixtech.passport.server.queue.BroadcastHandler;
import cn.luixtech.passport.server.queue.JobConsumer;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luixtech.springbootframework.utils.SseEmitterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SseConsumer implements BroadcastHandler {
    public static final String CHANNEL_SSE_BROADCAST = "sse";

    @Autowired
    public SseConsumer(JobConsumer jobConsumer) {
        // 注册为SSE作业处理器
        jobConsumer.registerBroadcastHandler(CHANNEL_SSE_BROADCAST, this);
    }

    @Override
    public void handleBroadcast(String payload) {
        SseMessage message = parsePayload(payload);
        if (message != null) {
            SseEmitterUtils.pushUserMessage(message.getUsername(), message.getMessage());
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