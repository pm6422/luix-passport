package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;
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
    public void enqueueJob(String jobType, Object payload) {
        // 使用Jackson或其他JSON库将payload转为JSON字符串
        String payloadJson = convertToJson(payload);

        JobQueue job = new JobQueue(jobType, payloadJson);
        jobQueueRepository.save(job);
    }

    private String convertToJson(Object payload) {
        try {
            return new ObjectMapper().writeValueAsString(payload);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to convert payload to JSON", e);
        }
    }

    // 批量添加作业
    @Transactional
    public void enqueueJobs(List<Pair<String, Object>> jobs) {
        List<JobQueue> jobEntities = jobs.stream()
                .map(job -> new JobQueue(job.getKey(), convertToJson(job.getValue())))
                .collect(Collectors.toList());

        jobQueueRepository.saveAll(jobEntities);
    }
}