package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.ScheduleExecutionLog;
import cn.luixtech.passport.server.repository.ScheduleExecutionLogRepository;
import cn.luixtech.passport.server.service.ScheduleExecutionLogService;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_DEVELOPER;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_DEVELOPER + "\")")
@Slf4j
public class ScheduleExecutionLogController {
    private final ScheduleExecutionLogRepository scheduleExecutionLogRepository;
    private final ScheduleExecutionLogService    scheduleExecutionLogService;

    @Operation(summary = "find schedule execution log list")
    @GetMapping("/api/schedule-execution-logs")
    public ResponseEntity<List<ScheduleExecutionLog>> find(@ParameterObject Pageable pageable,
                                                           @RequestParam(value = "scheduleName", required = false) String scheduleName,
                                                           @RequestParam(value = "status", required = false) String status) {
        Page<ScheduleExecutionLog> domains = scheduleExecutionLogService.find(pageable, scheduleName, status);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find schedule execution log by id")
    @GetMapping("/api/schedule-execution-logs/{id}")
    public ResponseEntity<ScheduleExecutionLog> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        ScheduleExecutionLog domain = scheduleExecutionLogRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "delete schedule execution log by id")
    @DeleteMapping("/api/schedule-execution-logs/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        scheduleExecutionLogRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
