package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.LoginLog;
import cn.luixtech.passport.server.service.LoginLogService;
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

import static cn.luixtech.passport.server.domain.Role.ROLE_ADMIN;

@RestController
@AllArgsConstructor
@Slf4j
public class LoginLogController {

  private final LoginLogService loginLogService;

  @Operation(summary = "find recent login logs")
  @GetMapping("/api/recent-logins")
  @PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
  public ResponseEntity<Page<LoginLog>> findRecentLogins(@ParameterObject Pageable pageable,
      @Parameter(description = "username") @RequestParam(value = "username", required = false) String username) {
    return ResponseEntity.ok(loginLogService.findRecentLogins(username, pageable));
  }
}
