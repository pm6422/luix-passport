package org.infinity.passport.controller;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

import javax.servlet.http.HttpServletResponse;

import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;

@RestController
@Api(tags = "系统")
public class SystemController {

    @Autowired
    private ApplicationProperties applicationProperties;

    @GetMapping(value = "/api/system/redis-admin", produces = MediaType.TEXT_HTML_VALUE)
    @Secured(Authority.DEVELOPER)
    public void redirectToRedisAdmin(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getRedis().getAdminUrl());
    }

    @GetMapping(value = "/api/system/scheduler-admin", produces = MediaType.TEXT_HTML_VALUE)
    @Secured(Authority.DEVELOPER)
    public void redirectToScheduler(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getScheduler().getAdminUrl());
    }

    @ApiOperation("获取IP")
    @GetMapping(value = "/api/system/ip")
    @Secured(Authority.DEVELOPER)
    public String getIp() throws UnknownHostException {
        return InetAddress.getLocalHost().getHostAddress();
    }
}
