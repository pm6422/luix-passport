package com.luixtech.passport.server.service;

import com.luixtech.passport.server.domain.LoginLog;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface LoginLogService {
  void recordLogin(String username, String email, String status, HttpServletRequest request);

  Page<LoginLog> findRecentLogins(String username, Pageable pageable);

  void deleteByUsername(String username);
}
