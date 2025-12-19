package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.LoginLog;
import cn.luixtech.passport.server.repository.LoginLogRepository;
import cn.luixtech.passport.server.service.LoginLogService;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import nl.basjes.parse.useragent.UserAgent;
import nl.basjes.parse.useragent.UserAgentAnalyzer;
import org.apache.commons.lang3.StringUtils;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.time.Instant;
import java.util.Map;
import java.util.concurrent.CompletableFuture;

@Slf4j
@Service
@AllArgsConstructor
public class LoginLogServiceImpl implements LoginLogService {

  private final LoginLogRepository loginLogRepository;
  private final RestTemplate restTemplate;
  private final UserAgentAnalyzer userAgentAnalyzer;

  @Override
  @Transactional
  public void recordLogin(String username, String status, HttpServletRequest request) {
    String xff = request.getHeader("X-Forwarded-For");
    String finalIp = xff != null ? xff.split(",")[0].trim() : request.getRemoteAddr();
    String userAgentString = request.getHeader("User-Agent");

    // Use CompletableFuture to run the heavy processing asynchronously within the
    // same service
    CompletableFuture.runAsync(() -> saveLoginLog(username, status, finalIp, userAgentString));
    log.info("Triggered login log record for user: {}, status: {}, ip: {}", username, status, finalIp);
  }

  private void saveLoginLog(String username, String status, String ip, String userAgentString) {
    LoginLog loginLog = new LoginLog();
    loginLog.setUsername(username);
    loginLog.setStatus(status);
    loginLog.setIp(ip);
    loginLog.setLoginAt(Instant.now());
    loginLog.setDevice(userAgentString);

    // Parse User-Agent
    if (StringUtils.isNotEmpty(userAgentString)) {
      try {
        UserAgent parsedUa = userAgentAnalyzer.parse(userAgentString);
        String browser = parsedUa.getValue(UserAgent.AGENT_NAME) + " " + parsedUa.getValue(UserAgent.AGENT_VERSION);
        String os = parsedUa.getValue(UserAgent.OPERATING_SYSTEM_NAME) + " "
            + parsedUa.getValue(UserAgent.OPERATING_SYSTEM_VERSION);
        loginLog.setBrowser(browser);
        loginLog.setOs(os);
        // Update device display for better readability
        loginLog.setDevice(browser + " / " + os);
      } catch (Exception e) {
        log.warn("Failed to parse user agent: {}", userAgentString, e);
      }
    }

    // Lookup Location
    if (StringUtils.isNotEmpty(ip) && !isLocalIp(ip)) {
      try {
        String url = "http://ip-api.com/json/" + ip + "?fields=status,country,city";
        Map<String, Object> response = restTemplate.exchange(
            url,
            HttpMethod.GET,
            null,
            new ParameterizedTypeReference<Map<String, Object>>() {
            }).getBody();

        if (response != null && "success".equals(response.get("status"))) {
          String city = (String) response.get("city");
          String country = (String) response.get("country");
          loginLog.setLocation(city + ", " + country);
        } else {
          loginLog.setLocation("Unknown");
        }
      } catch (Exception e) {
        log.warn("Failed to lookup location for IP: {}", ip, e);
        loginLog.setLocation("Unknown");
      }
    } else {
      loginLog.setLocation("Local/Unknown");
    }

    loginLogRepository.save(loginLog);
    log.debug("Async login log recorded for user: {}", username);
  }

  private boolean isLocalIp(String ip) {
    return "127.0.0.1".equals(ip) || "0:0:0:0:0:0:0:1".equals(ip) || ip.startsWith("192.168.") || ip.startsWith("10.");
  }

  @Override
  @Transactional(readOnly = true)
  public Page<LoginLog> findRecentLogins(String username, Pageable pageable) {
    if (username == null) {
      return loginLogRepository.findAll(pageable);
    }
    return loginLogRepository.findByUsernameOrderByLoginAtDesc(username, pageable);
  }

  @Override
  @Transactional
  public void deleteByUsername(String username) {
    // Implementation for clearing logs if needed
  }
}
