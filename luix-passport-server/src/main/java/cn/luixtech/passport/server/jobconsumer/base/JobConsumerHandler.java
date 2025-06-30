package cn.luixtech.passport.server.jobconsumer.base;

import cn.luixtech.passport.server.domain.JobQueue;

public interface JobConsumerHandler {
    void handle(JobQueue job) throws Exception;
}