package com.luixtech.passport.server.service.impl;

import com.luixtech.passport.server.domain.ScheduleLog;
import com.luixtech.passport.server.repository.ScheduleLogRepository;
import com.luixtech.passport.server.service.ScheduleLogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduleLogServiceImpl implements ScheduleLogService {
    private final ScheduleLogRepository scheduleLogRepository;

    @Override
    public Page<ScheduleLog> find(Pageable pageable, String scheduleName, String status) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("scheduleName", match -> match.contains().ignoreCase());

        ScheduleLog criteria = new ScheduleLog();
        criteria.setScheduleName(scheduleName);
        criteria.setStatus(status);

        Example<ScheduleLog> queryExample = Example.of(criteria, matcher);
        return scheduleLogRepository.findAll(queryExample, pageable);
    }
}
