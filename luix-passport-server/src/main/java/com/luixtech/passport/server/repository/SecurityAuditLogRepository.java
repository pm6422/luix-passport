package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;

public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, String> {
}
