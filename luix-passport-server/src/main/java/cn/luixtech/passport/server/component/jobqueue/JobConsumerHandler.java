package cn.luixtech.passport.server.component.jobqueue;

import cn.luixtech.passport.server.domain.JobQueue;

public interface JobConsumerHandler {
    void handle(JobQueue job) throws Exception;
}