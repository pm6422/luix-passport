package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.SchedulerLock;
import cn.luixtech.passport.server.repository.SchedulerLockRepository;
import cn.luixtech.passport.server.service.SchedulerLockService;
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

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class ScheduleController {
    private final SchedulerLockRepository schedulerLockRepository;
    private final SchedulerLockService    schedulerLockService;

    @Operation(summary = "find schedule list")
    @GetMapping("/api/schedules")
    public ResponseEntity<List<SchedulerLock>> find(@ParameterObject Pageable pageable,
                                                    @RequestParam(value = "id", required = false) String id) {
        Page<SchedulerLock> domains = schedulerLockService.find(pageable, id);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find schedule by id")
    @GetMapping("/api/schedules/{id}")
    public ResponseEntity<SchedulerLock> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        SchedulerLock domain = schedulerLockRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "delete schedule by id")
    @DeleteMapping("/api/schedules/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        schedulerLockRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }
}
