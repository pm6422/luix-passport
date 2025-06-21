package cn.luixtech.passport.server.queue;

public interface BroadcastHandler {
    void handleBroadcast(String payload) throws Exception;
}