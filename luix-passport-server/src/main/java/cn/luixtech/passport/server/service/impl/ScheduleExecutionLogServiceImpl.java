package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import cn.luixtech.passport.server.repository.ScheduleExecutionLogRepository;
import cn.luixtech.passport.server.service.ScheduleExecutionLogService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ScheduleExecutionLogServiceImpl implements ScheduleExecutionLogService {
    private final ScheduleExecutionLogRepository scheduleExecutionLogRepository;

    @Override
    public Page<ScheduleExecutionLog> find(Pageable pageable, String scheduleName, String status) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("scheduleName", match -> match.contains().ignoreCase());

        ScheduleExecutionLog criteria = new ScheduleExecutionLog();
        criteria.setScheduleName(scheduleName);
        criteria.setStatus(status);

        Example<ScheduleExecutionLog> queryExample = Example.of(criteria, matcher);
        return scheduleExecutionLogRepository.findAll(queryExample, pageable);
    }
}
