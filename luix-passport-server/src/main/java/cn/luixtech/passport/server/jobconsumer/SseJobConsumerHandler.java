package cn.luixtech.passport.server.jobconsumer;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.pojo.SseMessage;
import cn.luixtech.passport.server.component.jobqueue.JobConsumer;
import cn.luixtech.passport.server.jobconsumer.base.JobConsumerHandler;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import com.luixtech.springbootframework.utils.SseEmitterUtils;

@Component
@Slf4j
public class SseJobConsumerHandler implements JobConsumerHandler {
    public static final String CHANNEL_SSE = "sse";

    public SseJobConsumerHandler(JobConsumer jobConsumer) {
        // register p2p consumer handler
        jobConsumer.registerPointToPointHandler(CHANNEL_SSE, this);
    }

    @Override
    public void handle(JobQueue job) {
        SseMessage message = parsePayload(job.getPayload());
        if (message != null) {
            SseEmitterUtils.pushToUser(message.getUsername(), message.getMessage());
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
