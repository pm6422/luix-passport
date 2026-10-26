package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.LoginLog;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

public interface LoginLogRepository extends JpaRepository<LoginLog, String> {

  Page<LoginLog> findByUsernameOrderByLoginAtDesc(String username, Pageable pageable);

  long countByLoginAtBetweenAndStatus(java.time.Instant start, java.time.Instant end, String status);

  @org.springframework.data.jpa.repository.Query("select count(distinct l.username) from LoginLog l where l.loginAt >= :start and l.loginAt < :end and l.status = :status")
  long countUniqueUsersByLoginAtBetweenAndStatus(java.time.Instant start, java.time.Instant end, String status);
}
