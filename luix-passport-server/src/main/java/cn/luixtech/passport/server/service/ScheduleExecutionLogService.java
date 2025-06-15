package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleExecutionLogService {
    Page<ScheduleExecutionLog> find(Pageable pageable, String scheduleName, String status);
}
