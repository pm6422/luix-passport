package com.luixtech.passport.controller;

import com.luixtech.framework.config.LuixProperties;
import com.luixtech.passport.domain.Authority;
import com.luixtech.utilities.network.AddressUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.info.BuildProperties;
import org.springframework.context.ApplicationContext;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.luixtech.framework.config.api.SpringDocConfiguration.AUTH;


@RestController
@SecurityRequirement(name = AUTH)
@Slf4j
public class SystemController {
    @Resource
    private Environment                          env;
    @Resource
    private LuixProperties                       luixProperties;
    @Value("${app.id}")
    private String                               appId;
    @Value("${app.version}")
    private String                               appVersion;
    @Value("${app.companyName}")
    private String                               companyName;
    @Value("${springdoc.api-docs.enabled}")
    private boolean                              enabledApiDocs;
    @Resource
    private ApplicationContext                   applicationContext;
    @Autowired(required = false)
    private BuildProperties                      buildProperties;

    @GetMapping(value = "app/constants.js", produces = "application/javascript")
    public String getConstantsJs() {
        String id = buildProperties != null ? buildProperties.getArtifact() : appId;
        String version = buildProperties != null ? buildProperties.getVersion() : appVersion;
        String js = """
                'use strict';
                (function () {
                    'use strict';
                    angular
                        .module('smartcloudserviceApp')
                        .constant('APP_NAME', '%s')
                        .constant('VERSION', '%s')
                        .constant('COMPANY', '%s')
                        .constant('RIBBON_PROFILE', '%s')
                        .constant('ENABLE_SWAGGER', '%s')
                        .constant('PAGINATION_CONSTANTS', {
                            'itemsPerPage': 10
                        })
                        .constant('DEBUG_INFO_ENABLED', true);
                })();""";

        return String.format(js, id, version, companyName, getRibbonProfile(), enabledApiDocs);
    }

    private String getRibbonProfile() {
        List<String> displayOnActiveProfiles = luixProperties.getRibbon().getDisplayOnActiveProfiles();
        if (CollectionUtils.isEmpty(displayOnActiveProfiles)) {
            return null;
        }

        displayOnActiveProfiles.retainAll(Arrays.asList(env.getActiveProfiles()));

        return CollectionUtils.isNotEmpty(displayOnActiveProfiles) ? displayOnActiveProfiles.get(0) : StringUtils.EMPTY;
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

}
