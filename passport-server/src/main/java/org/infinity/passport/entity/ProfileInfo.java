package org.infinity.passport.entity;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import org.infinity.passport.config.ApplicationConstants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ApiModel("Profile信息")
public class ProfileInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "有效Profiles")
    private String[]          activeProfiles;

    @ApiModelProperty(value = "环境丝带")
    private String            ribbonEnv;

    @ApiModelProperty(value = "是否生产环境")
    private boolean           inProduction     = false;

    @ApiModelProperty(value = "是否禁止Swagger")
    private boolean           swaggerDisabled  = false;

    public ProfileInfo(String[] activeProfiles, String ribbonEnv) {
        this.activeProfiles = activeProfiles;
        this.ribbonEnv = ribbonEnv;

        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        if (springBootProfiles.contains(ApplicationConstants.SPRING_PROFILE_PRODUCTION)) {
            this.inProduction = true;
        }
        if (springBootProfiles.contains(ApplicationConstants.SPRING_PROFILE_NO_SWAGGER)) {
            this.swaggerDisabled = true;
        }
    }

    public String[] getActiveProfiles() {
        return activeProfiles;
    }

    public void setActiveProfiles(String[] activeProfiles) {
        this.activeProfiles = activeProfiles;
    }

    public String getRibbonEnv() {
        return ribbonEnv;
    }

    public void setRibbonEnv(String ribbonEnv) {
        this.ribbonEnv = ribbonEnv;
    }

    public boolean isInProduction() {
        return inProduction;
    }

    public void setInProduction(boolean inProduction) {
        this.inProduction = inProduction;
    }

    public boolean isSwaggerDisabled() {
        return swaggerDisabled;
    }

    public void setSwaggerDisabled(boolean swaggerDisabled) {
        this.swaggerDisabled = swaggerDisabled;
    }
}