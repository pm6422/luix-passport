package org.infinity.passport.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A DTO representing a app authority.
 */
@ApiModel("应用权限DTO")
public class AppAuthorityDTO implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    private String            id;

    @ApiModelProperty(value = "应用名称")
    private String            appName;

    @ApiModelProperty(value = "权限名称")
    private String            authorityName;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

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

    @Override
    public String toString() {
        return "AppAuthorityDTO [id=" + id + ", appName=" + appName + ", authorityName=" + authorityName + "]";
    }

}