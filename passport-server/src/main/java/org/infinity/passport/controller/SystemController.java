package org.infinity.passport.controller;

import com.github.cloudyrock.mongock.runner.core.executor.MongockRunnerBase;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.dto.SystemDTO;
import org.infinity.passport.utils.NetworkUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@RestController
@Api(tags = "系统管理")
@Slf4j
public class SystemController {

    @Resource
    private Environment           env;
    @Resource
    private ApplicationProperties applicationProperties;
    @Resource
    private MongockRunnerBase     mongockRunnerBase;
    @Resource
    private MongoTemplate         mongoTemplate;
    @Resource
    private ApplicationContext    applicationContext;
    @Value("${app.id}")
    private String                appId;
    @Value("${app.version}")
    private String                appVersion;
    @Value("${app.companyName}")
    private String                companyName;

    @ApiOperation("get system info")
    @GetMapping("/open-api/systems/info")
    public ResponseEntity<SystemDTO> getSystemInfo() {
        SystemDTO systemDTO = new SystemDTO(appId, appVersion, companyName, getRibbonProfile(),
                applicationProperties.getSwagger().isEnabled(), env.getActiveProfiles());
        return ResponseEntity.ok(systemDTO);
    }

    private String getRibbonProfile() {
        String[] displayOnActiveProfiles = applicationProperties.getRibbon().getDisplayOnActiveProfiles();
        if (ArrayUtils.isEmpty(displayOnActiveProfiles)) {
            return null;
        }

        List<String> ribbonProfiles = Stream.of(displayOnActiveProfiles).collect(Collectors.toList());
        ribbonProfiles.retainAll(Arrays.asList(env.getActiveProfiles()));

        return CollectionUtils.isNotEmpty(ribbonProfiles) ? ribbonProfiles.get(0) : null;
    }

    @ApiOperation("get bean")
    @GetMapping("/api/systems/bean")
    public ResponseEntity<Object> getBean(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(applicationContext.getBean(name));
    }

    @GetMapping("/api/systems/redis-admin")
    @Secured(Authority.DEVELOPER)
    public void redirectToRedisAdmin(HttpServletResponse response) throws IOException {
        response.sendRedirect(applicationProperties.getRedis().getAdminUrl());
    }

    @GetMapping("/api/systems/intranet-ip")
    @Secured(Authority.DEVELOPER)
    public ResponseEntity<String> getIntranetIp() {
        return ResponseEntity.ok(NetworkUtils.INTRANET_IP);
    }

    @GetMapping("/open-api/systems/reset-database")
    public String resetDatabase() {
        mongoTemplate.getDb().drop();
        mongockRunnerBase.execute();
        return "Reset successfully.";
    }
}
