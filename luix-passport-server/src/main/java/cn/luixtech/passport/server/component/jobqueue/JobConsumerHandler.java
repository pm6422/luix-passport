package cn.luixtech.passport.server.component.jobqueue;

import cn.luixtech.passport.server.domain.JobQueue;

public interface JobConsumerHandler {
    String getChannel();

    void handle(JobQueue job) throws Exception;
}