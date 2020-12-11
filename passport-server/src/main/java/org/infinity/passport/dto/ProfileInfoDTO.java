package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinity.passport.config.ApplicationConstants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;

@ApiModel("Profile信息")
@Data
@NoArgsConstructor
public class ProfileInfoDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "有效Profiles")
    private String[] activeProfiles;

    @ApiModelProperty(value = "环境丝带")
    private String ribbonEnv;

    @ApiModelProperty(value = "是否生产环境")
    private boolean inProduction = false;

    @ApiModelProperty(value = "是否启用Swagger")
    private boolean swaggerEnabled;

    public ProfileInfoDTO(String[] activeProfiles, boolean swaggerEnabled, String ribbonEnv) {
        this.activeProfiles = activeProfiles;
        this.ribbonEnv = ribbonEnv;

        List<String> springBootProfiles = Arrays.asList(activeProfiles);
        if (springBootProfiles.contains(ApplicationConstants.SPRING_PROFILE_PROD)) {
            this.inProduction = true;
        }
        this.swaggerEnabled = swaggerEnabled;
    }
}