package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.ScheduleLog;
import cn.luixtech.passport.server.repository.ScheduleLogRepository;
import cn.luixtech.passport.server.service.ScheduleLogService;
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

import static cn.luixtech.passport.server.domain.Role.ROLE_DEVELOPER;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_DEVELOPER + "\")")
@Slf4j
public class ScheduleLogController {
    private final ScheduleLogRepository scheduleLogRepository;
    private final ScheduleLogService    scheduleLogService;

    @Operation(summary = "find schedule log list")
    @GetMapping("/api/schedule-logs")
    public ResponseEntity<Page<ScheduleLog>> find(@ParameterObject Pageable pageable,
                                                  @RequestParam(value = "scheduleName", required = false) String scheduleName,
                                                  @RequestParam(value = "status", required = false) String status) {
        Page<ScheduleLog> domains = scheduleLogService.find(pageable, scheduleName, status);
        return ResponseEntity.ok(domains);
    }

    @Operation(summary = "find schedule log by id")
    @GetMapping("/api/schedule-logs/{id}")
    public ResponseEntity<ScheduleLog> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        ScheduleLog domain = scheduleLogRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "delete schedule log by id")
    @DeleteMapping("/api/schedule-logs/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        scheduleLogRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
