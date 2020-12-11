package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.util.List;

@ApiModel("管理系统菜单列表DTO")
@Data
public class AdminAuthorityMenusDTO implements Serializable {

    private static final long serialVersionUID = -3119877507448443380L;

    @ApiModelProperty("应用名称")
    @NotNull
    private String appName;

    @ApiModelProperty("权限名")
    @NotNull
    private String authorityName;

    @ApiModelProperty("管理系统菜单IDs")
    private List<String> adminMenuIds;

}
