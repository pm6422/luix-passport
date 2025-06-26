package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;
import cn.luixtech.passport.server.exception.JobQueueException;
import cn.luixtech.passport.server.repository.JobQueueRepository;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.tuple.Pair;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Component
@AllArgsConstructor
public class JobProducer {
    private final JobQueueRepository jobQueueRepository;

    @Transactional
    public void enqueue(String channel, Object payload) {
        JobQueue job = createJob(channel, payload);
        jobQueueRepository.save(job);
    }

    @Transactional
    public void enqueueBatch(List<Pair<String, Object>> jobs) {
        List<JobQueue> entities = jobs.stream()
                .map(job -> createJob(job.getKey(), job.getValue()))
                .collect(Collectors.toList());
        jobQueueRepository.saveAll(entities);
    }

    private JobQueue createJob(String channel, Object payload) {
        return new JobQueue(channel, serializePayload(payload));
    }

    private String serializePayload(Object payload) {
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new JobQueueException("Failed to serialize payload", e);
        }
    }
}