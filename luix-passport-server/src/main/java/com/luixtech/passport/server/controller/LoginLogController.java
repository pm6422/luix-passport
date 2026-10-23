package com.luixtech.passport.server.controller;

import com.luixtech.passport.server.domain.LoginLog;
import com.luixtech.passport.server.repository.LoginLogRepository;
import com.luixtech.passport.server.service.LoginLogService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.luixtech.passport.server.domain.Role.ROLE_ADMIN;

@RestController
@AllArgsConstructor
@Slf4j
public class LoginLogController {

  private final LoginLogService loginLogService;
  private final LoginLogRepository loginLogRepository;

  @Operation(summary = "find recent login logs")
  @GetMapping("/api/login-logs/recent")
  @PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
  public ResponseEntity<Page<LoginLog>> findRecentLogins(@ParameterObject Pageable pageable,
      @Parameter(description = "username") @RequestParam(value = "username", required = false) String username) {
    return ResponseEntity.ok(loginLogService.findRecentLogins(username, pageable));
  }

  @Operation(summary = "get login activity statistics")
  @GetMapping("/api/login-logs/activity")
  @PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
  public ResponseEntity<List<Map<String, Object>>> getActivityStats(
      @Parameter(description = "days") @RequestParam(defaultValue = "7") int days) {
    List<Map<String, Object>> activity = new ArrayList<>();
    LocalDate today = LocalDate.now();
    for (int i = days - 1; i >= 0; i--) {
      LocalDate date = today.minusDays(i);
      Instant startOfDay = date.atStartOfDay(ZoneId.systemDefault()).toInstant();
      Instant endOfDay = date.plusDays(1).atStartOfDay(ZoneId.systemDefault()).toInstant();

      long logins = loginLogRepository.countByLoginAtBetweenAndStatus(startOfDay, endOfDay, "success");
      long activeUsers = loginLogRepository.countUniqueUsersByLoginAtBetweenAndStatus(startOfDay, endOfDay, "success");

      Map<String, Object> dayStats = new HashMap<>();
      dayStats.put("name", date.getDayOfWeek().name().substring(0, 3));
      dayStats.put("users", activeUsers);
      dayStats.put("logins", logins);
      activity.add(dayStats);
    }
    return ResponseEntity.ok(activity);
  }
}
