package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.ScheduleLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ScheduleLogService {
    Page<ScheduleLog> find(Pageable pageable, String scheduleName, String status);
}
