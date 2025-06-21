package cn.luixtech.passport.server.queue.consumer;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.pojo.SseMessage;
import cn.luixtech.passport.server.queue.JobConsumer;
import cn.luixtech.passport.server.queue.JobHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.luixtech.springbootframework.utils.SseEmitterUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
@Slf4j
public class SseConsumer implements JobHandler {
    public static final String TOPIC_SSE_BROADCAST = "sse_broadcast";

    @Autowired
    public SseConsumer(JobConsumer jobConsumer) {
        // 注册为SSE作业处理器
        jobConsumer.registerHandler(this);
    }

    @Override
    public String getJobType() {
        return TOPIC_SSE_BROADCAST;
    }

    @Override
    public void accept(JobQueue job) {
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