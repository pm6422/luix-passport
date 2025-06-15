package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.repository.SchedulerLockRepository;
import cn.luixtech.passport.server.service.SchedulerLockService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
@AllArgsConstructor
public class SchedulerLockServiceImpl implements SchedulerLockService {
    private final SchedulerLockRepository schedulerLockRepository;

    @Override
    public boolean isLockHeld(String id, String lockedBy) {
        return schedulerLockRepository.countByIdAndLockedByAndLockUntilAfter(id, lockedBy, Instant.now()) > 0;
    }
}
