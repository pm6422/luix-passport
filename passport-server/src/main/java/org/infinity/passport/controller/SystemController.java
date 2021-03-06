package org.infinity.passport.controller;

import io.changock.runner.core.ChangockBase;
import io.swagger.annotations.Api;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.utils.NetworkUtils;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = "系统")
@Slf4j
public class SystemController {

    private final ApplicationProperties applicationProperties;
    private final ChangockBase          changockBase;
    private final MongoTemplate         mongoTemplate;

    public SystemController(ApplicationProperties applicationProperties, ChangockBase changockBase, MongoTemplate mongoTemplate) {
        this.applicationProperties = applicationProperties;
        this.changockBase = changockBase;
        this.mongoTemplate = mongoTemplate;
    }

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

    @GetMapping("/api/system/internet-ip")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<String> getInternetIp() {
        return ResponseEntity.ok(NetworkUtils.INTERNET_IP);
    }

    @GetMapping("/api/system/intranet-ip")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<String> getIntranetIp() {
        return ResponseEntity.ok(NetworkUtils.INTRANET_IP);
    }

    @GetMapping("/open-api/system/reset-database")
    public String resetDatabase() {
        mongoTemplate.getDb().drop();
        changockBase.execute();
        return "Reset successfully.";
    }
}
