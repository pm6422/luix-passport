package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.SchedulerLock;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;

public interface SchedulerLockRepository extends JpaRepository<SchedulerLock, String> {

    long countByIdAndLockedByAndLockUntilAfter(String id, String lockedBy, Instant lockUntil);
}
