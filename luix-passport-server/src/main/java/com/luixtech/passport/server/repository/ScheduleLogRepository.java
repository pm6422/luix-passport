package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.ScheduleLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface ScheduleLogRepository extends JpaRepository<ScheduleLog, String> {

}
