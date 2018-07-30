package org.infinity.passport.dto;

import java.io.Serializable;
import java.util.List;

import javax.validation.constraints.NotNull;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("管理系统菜单列表DTO")
public class AdminAuthorityMenusDTO implements Serializable {

    private static final long serialVersionUID = -3119877507448443380L;

    @ApiModelProperty("应用名称")
    @NotNull
    private String            appName;

    @ApiModelProperty("权限名")
    @NotNull
    private String            authorityName;

    @ApiModelProperty("管理系统菜单IDs")
    private List<String>      adminMenuIds;

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    public List<String> getAdminMenuIds() {
        return adminMenuIds;
    }

    public void setAdminMenuIds(List<String> adminMenuIds) {
        this.adminMenuIds = adminMenuIds;
    }

    @Override
    public String toString() {
        return "AdminAuthorityMenusDTO [appName=" + appName + ", authorityName=" + authorityName + ", adminMenuIds="
                + adminMenuIds + "]";
    }
}
