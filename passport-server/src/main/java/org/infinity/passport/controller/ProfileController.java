package org.infinity.passport.controller;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.entity.ProfileInfo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.env.Environment;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@RestController
@Api(tags = "系统环境")
public class ProfileController {

    @Autowired
    private Environment           env;

    @Autowired
    private ApplicationProperties applicationProperties;

    @ApiOperation("获取系统Profile")
    @GetMapping("/open-api/profile-info")
    public ResponseEntity<ProfileInfo> getProfileInfo() {
        ProfileInfo profileInfo = new ProfileInfo(env.getActiveProfiles(), getRibbonEnv());
        return ResponseEntity.ok(profileInfo);
    }

    private String getRibbonEnv() {
        String[] activeProfiles = env.getActiveProfiles();
        String[] displayOnActiveProfiles = applicationProperties.getRibbon().getDisplayOnActiveProfiles();

        if (displayOnActiveProfiles == null) {
            return null;
        }

        List<String> ribbonProfiles = new ArrayList<>(Arrays.asList(displayOnActiveProfiles));
        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        ribbonProfiles.retainAll(springBootProfiles);

        if (ribbonProfiles.size() > 0) {
            return ribbonProfiles.get(0);
        }
        return null;
    }
}
