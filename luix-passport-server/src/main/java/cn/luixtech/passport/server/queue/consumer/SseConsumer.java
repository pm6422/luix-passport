package cn.luixtech.passport.server.queue.consumer;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.pojo.SseMessage;
import cn.luixtech.passport.server.queue.JobConsumer;
import cn.luixtech.passport.server.queue.consumer.base.JobConsumerHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luixtech.springbootframework.utils.SseEmitterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SseConsumer implements JobConsumerHandler {
    public static final String CHANNEL_SSE = "sse";

    @Autowired
    public SseConsumer(JobConsumer jobConsumer) {
        // 注册为SSE作业处理器
        jobConsumer.registerPointToPointHandler(CHANNEL_SSE, this);
    }

    @Override
    public void handle(JobQueue job) {
        SseMessage message = parsePayload(job.getPayload());
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