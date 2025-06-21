package cn.luixtech.passport.server.queue;

import cn.luixtech.passport.server.domain.JobQueue;

public interface BroadcastHandler {
    void handleBroadcast(JobQueue job) throws Exception;
}