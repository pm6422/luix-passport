package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link ScheduleExecutionLog} entity.
 */
@Repository
public interface ScheduleExecutionLogRepository extends JpaRepository<ScheduleExecutionLog, String> {

}
