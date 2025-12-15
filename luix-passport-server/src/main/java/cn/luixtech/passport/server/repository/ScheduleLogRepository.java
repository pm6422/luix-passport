package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.ScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleLogRepository extends JpaRepository<ScheduleLog, String> {

}
