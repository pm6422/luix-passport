package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.SchedulerLock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface SchedulerLockService {
    Page<SchedulerLock> find(Pageable pageable, String id);
}
