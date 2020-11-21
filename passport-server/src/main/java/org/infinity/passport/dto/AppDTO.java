package org.infinity.passport.dto;

import java.io.Serializable;
import java.util.Set;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

/**
 * A DTO representing an App.
 */
@ApiModel("应用DTO")
public class AppDTO implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    @ApiModelProperty(value = "应用名称")
    private String            name;

    @ApiModelProperty(value = "是否可用")
    private Boolean           enabled;

    @ApiModelProperty(value = "权限名称")
    private Set<String>       authorities;

    public AppDTO(String name, Boolean enabled) {
        super();
        this.name = name;
        this.enabled = enabled;
    }

    public AppDTO(String name, Boolean enabled, Set<String> authorities) {
        super();
        this.name = name;
        this.enabled = enabled;
        this.authorities = authorities;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Set<String> getAuthorities() {
        return authorities;
    }

    public void setAuthorities(Set<String> authorities) {
        this.authorities = authorities;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    @Override
    public String toString() {
        return "AppDTO [name=" + name + ", enabled=" + enabled + ", authorities=" + authorities + "]";
    }
}