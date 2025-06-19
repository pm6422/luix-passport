package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;

public interface JobHandler {
    String getJobType();

    void handle(JobQueue job) throws Exception;
}