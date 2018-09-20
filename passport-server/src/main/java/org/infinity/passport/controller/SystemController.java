package org.infinity.passport.controller;

import com.github.mongobee.Mongobee;
import com.github.mongobee.exception.MongobeeException;
import io.swagger.annotations.Api;
import org.infinity.passport.config.ApplicationConstants;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@RestController
@Api(tags = "系统")
public class SystemController {

    @Autowired
    private ApplicationProperties applicationProperties;
    @Autowired
    private Mongobee              mongobee;
    @Autowired
    private MongoTemplate         mongoTemplate;

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
    public ResponseEntity<String> getIp() {
        return ResponseEntity.ok(ApplicationConstants.SERVER_IP);
    }

    @GetMapping("/open-api/system/reset-database")
    public String resetDatabase() throws MongobeeException {
        mongoTemplate.getDb().drop();
        mongobee.execute();
        return "Reset successfully.";
    }
}
