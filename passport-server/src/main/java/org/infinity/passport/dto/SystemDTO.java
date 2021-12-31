package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.Validate;
import org.infinity.passport.config.ApplicationConstants;

import java.io.Serializable;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@ApiModel("系统信息")
@Data
@NoArgsConstructor
public class SystemDTO implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "应用ID")
    private String appId;

    @ApiModelProperty(value = "应用版本")
    private String appVersion;

    @ApiModelProperty(value = "公司名称")
    private String companyName;

    @ApiModelProperty(value = "主要Profile")
    private String mainProfile;

    @ApiModelProperty(value = "丝带Profile")
    private String ribbonProfile;

    @ApiModelProperty(value = "是否启用Swagger")
    private Boolean swaggerEnabled;

    @ApiModelProperty(value = "是否为生产环境")
    private Boolean inProduction;

    public SystemDTO(String appId, String appVersion, String companyName, String ribbonProfile,
                     boolean swaggerEnabled, String[] activeProfiles) {
        Validate.notEmpty(activeProfiles, "Please specify profiles!");

        this.appId = appId;
        this.appVersion = appVersion;
        this.companyName = companyName;

        List<String> availableProfiles = Stream.of(ApplicationConstants.AVAILABLE_PROFILES).collect(Collectors.toList());
        availableProfiles.retainAll(Arrays.asList(activeProfiles));
        Validate.notEmpty(availableProfiles, "Please specify available profiles!");
        this.mainProfile = availableProfiles.get(0);

        this.ribbonProfile = ribbonProfile;
        this.swaggerEnabled = swaggerEnabled;

        this.inProduction = Arrays.asList(activeProfiles).contains(ApplicationConstants.SPRING_PROFILE_PROD);
    }
}