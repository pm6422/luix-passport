package org.infinity.passport.controller;

import com.luixtech.utilities.network.AddressUtils;
import io.mongock.api.config.MongockConfiguration;
import io.mongock.driver.api.driver.ConnectionDriver;
import io.mongock.driver.mongodb.springdata.v3.config.MongoDBConfiguration;
import io.mongock.driver.mongodb.springdata.v3.config.SpringDataMongoV3Context;
import io.mongock.runner.springboot.MongockSpringboot;
import io.mongock.runner.springboot.RunnerSpringbootBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.env.Environment;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.transaction.PlatformTransactionManager;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;

@RestController
@SecurityRequirement(name = AUTH)
@Slf4j
public class SystemController {
    @Resource
    private Environment                          env;
    @Resource
    private ApplicationProperties                applicationProperties;
    @Value("${app.id}")
    private String                               appId;
    @Value("${app.version}")
    private String                               appVersion;
    @Value("${app.companyName}")
    private String                               companyName;
    @Value("${springdoc.api-docs.enabled}")
    private boolean                              enabledApiDocs;
    @Resource
    private MongoTemplate                        mongoTemplate;
    @Resource
    private ApplicationContext                   applicationContext;
    @Autowired(required = false)
    private BuildProperties                      buildProperties;
    @Resource
    private MongockConfiguration                 mongockConfiguration;
    @Resource
    private ApplicationEventPublisher            applicationEventPublisher;
    @Resource
    private MongoDBConfiguration                 mongoDBConfiguration;
    @Resource
    private Optional<PlatformTransactionManager> txManagerOpt;

    @GetMapping(value = "app/constants.js", produces = "application/javascript")
    public String getConstantsJs() {
        String id = buildProperties != null ? buildProperties.getArtifact() : appId;
        String version = buildProperties != null ? buildProperties.getVersion() : appVersion;
        String js = "'use strict';\n" +
                "(function () {\n" +
                "    'use strict';\n" +
                "    angular\n" +
                "        .module('smartcloudserviceApp')\n" +
                "        .constant('APP_NAME', '%s')\n" +
                "        .constant('VERSION', '%s')\n" +
                "        .constant('COMPANY', '%s')\n" +
                "        .constant('RIBBON_PROFILE', '%s')\n" +
                "        .constant('ENABLE_SWAGGER', '%s')\n" +
                "        .constant('PAGINATION_CONSTANTS', {\n" +
                "            'itemsPerPage': 10\n" +
                "        })\n" +
                "        .constant('DEBUG_INFO_ENABLED', true);\n" +
                "})();";

        return String.format(js, id, version, companyName, getRibbonProfile(), enabledApiDocs);
    }

    private String getRibbonProfile() {
        String[] displayOnActiveProfiles = applicationProperties.getRibbon().getDisplayOnActiveProfiles();
        if (ArrayUtils.isEmpty(displayOnActiveProfiles)) {
            return null;
        }

        List<String> ribbonProfiles = Stream.of(displayOnActiveProfiles).collect(Collectors.toList());
        ribbonProfiles.retainAll(Arrays.asList(env.getActiveProfiles()));

        return CollectionUtils.isNotEmpty(ribbonProfiles) ? ribbonProfiles.get(0) : StringUtils.EMPTY;
    }

    @Operation(summary = "get ribbon profile")
    @GetMapping("/open-api/systems/ribbon-profile")
    public ResponseEntity<String> getRibbonProfileInfo() {
        return ResponseEntity.ok(getRibbonProfile());
    }

    @Operation(summary = "introspect bean")
    @GetMapping("/api/systems/bean")
    public ResponseEntity<Object> introspectBean(@RequestParam(value = "name") String name) {
        return ResponseEntity.ok(applicationContext.getBean(name));
    }

    @GetMapping("/api/systems/intranet-ip")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<String> getIntranetIp() {
        return ResponseEntity.ok(AddressUtils.getIntranetIp());
    }

    @Operation(summary = "reset database")
    @GetMapping("/open-api/systems/reset-database")
    public String resetDatabase() {
        mongoTemplate.getDb().drop();
        ConnectionDriver connectionDriver = new SpringDataMongoV3Context()
                .connectionDriver(mongoTemplate, mongockConfiguration, mongoDBConfiguration, txManagerOpt);
        RunnerSpringbootBuilder runnerSpringbootBuilder = MongockSpringboot.builder()
                .setDriver(connectionDriver)
                .setConfig(mongockConfiguration)
                .setSpringContext(applicationContext)
                .setEventPublisher(applicationEventPublisher);
        runnerSpringbootBuilder.buildRunner().execute();
        return "Reset database successfully.";
    }
}
