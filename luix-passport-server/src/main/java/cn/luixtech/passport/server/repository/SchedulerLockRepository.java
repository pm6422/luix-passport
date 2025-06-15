package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.SchedulerLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;

/**
 * Spring Data JPA repository for the {@link SchedulerLock} entity.
 */
@Repository
public interface SchedulerLockRepository extends JpaRepository<SchedulerLock, String> {

    long countByIdAndLockedByAndLockUntilAfter(String id, String lockedBy, Instant lockUntil);
}
