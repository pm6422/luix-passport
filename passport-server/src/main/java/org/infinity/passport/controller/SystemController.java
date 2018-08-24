package org.infinity.passport.controller;

import io.swagger.annotations.Api;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.net.UnknownHostException;

@RestController
@Api(tags = "系统")
public class SystemController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @GetMapping("/api/system/redis-admin")
    @Secured(Authority.DEVELOPER)
    public void redirectToRedisAdmin(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getRedis().getAdminUrl());
    }

    @GetMapping("/api/system/scheduler-admin")
    @Secured(Authority.DEVELOPER)
    public void redirectToScheduler(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getScheduler().getAdminUrl());
    }

    @GetMapping("/api/system/ip")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<String> getIp() throws UnknownHostException {
        return ResponseEntity.ok(ApplicationConstants.SERVER_IP);
    }
}
