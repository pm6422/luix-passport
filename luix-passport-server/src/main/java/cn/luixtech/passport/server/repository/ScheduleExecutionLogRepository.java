package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleExecutionLogRepository extends JpaRepository<ScheduleExecutionLog, String> {

}
