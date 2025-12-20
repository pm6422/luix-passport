package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.SecurityAuditLog;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface SecurityAuditLogRepository extends JpaRepository<SecurityAuditLog, String> {
}
