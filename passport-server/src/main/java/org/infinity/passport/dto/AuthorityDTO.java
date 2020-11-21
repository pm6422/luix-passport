package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;

/**
 * A DTO representing a authority.
 */
@ApiModel("权限DTO")
@Data
@EqualsAndHashCode
public class AuthorityDTO implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    @ApiModelProperty(value = "权限名称")
    private String name;

    @ApiModelProperty(value = "是否为系统权限")
    private Boolean systemLevel;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

    public AuthorityDTO(String name, Boolean systemLevel, Boolean enabled) {
        super();
        this.name = name;
        this.systemLevel = systemLevel;
        this.enabled = enabled;
    }
}