package cn.luixtech.passport.server.service;

public interface SchedulerLockService {

    boolean isLockHeld(String id, String lockedBy);
}
