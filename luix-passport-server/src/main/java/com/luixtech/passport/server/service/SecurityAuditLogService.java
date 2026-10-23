package com.luixtech.passport.server.service;

import com.luixtech.passport.server.domain.SecurityAuditLog;
import com.luixtech.passport.server.repository.SecurityAuditLogRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SecurityAuditLogService {

  private final SecurityAuditLogRepository securityAuditLogRepository;

  public Page<SecurityAuditLog> find(Pageable pageable) {
    return securityAuditLogRepository.findAll(pageable);
  }

  public void log(String principal, String action, String severity, String userAgent, String ip, String location,
      String details) {
    SecurityAuditLog log = new SecurityAuditLog();
    log.setPrincipal(principal);
    log.setAction(action);
    log.setSeverity(severity);
    log.setUserAgent(userAgent);
    log.setIp(ip);
    log.setLocation(location);
    log.setDetails(details);
    securityAuditLogRepository.save(log);
  }
}
