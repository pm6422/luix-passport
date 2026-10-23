package com.luixtech.passport.server.component.jobqueue;

import com.luixtech.passport.server.domain.JobQueue;

public interface JobConsumerHandler {
    String getChannel();

    void handle(JobQueue job) throws Exception;
}