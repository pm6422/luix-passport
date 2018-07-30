package org.infinity.passport.dto;

import java.io.Serializable;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A DTO representing a authority.
 */
@ApiModel("权限DTO")
public class AuthorityDTO implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    @ApiModelProperty(value = "权限名称")
    private String            name;

    @ApiModelProperty(value = "是否为系统权限")
    private Boolean           systemLevel;

    @ApiModelProperty(value = "是否可用")
    private Boolean           enabled;

    public AuthorityDTO() {
    }

    public AuthorityDTO(String name, Boolean systemLevel, Boolean enabled) {
        super();
        this.name = name;
        this.systemLevel = systemLevel;
        this.enabled = enabled;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getSystemLevel() {
        return systemLevel;
    }

    public void setSystemLevel(Boolean systemLevel) {
        this.systemLevel = systemLevel;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "AuthorityDTO [name=" + name + ", systemLevel=" + systemLevel + ", enabled=" + enabled + "]";
    }
}