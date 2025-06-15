package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.SchedulerLock;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link SchedulerLock} entity.
 */
@Repository
public interface SchedulerLockRepository extends JpaRepository<SchedulerLock, String> {

}
