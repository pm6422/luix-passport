package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.io.Serializable;

/**
 * A DTO representing a app authority.
 */
@ApiModel("应用权限DTO")
@Data
public class AppAuthorityDTO implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    private String id;

    @ApiModelProperty(value = "应用名称")
    private String appName;

    @ApiModelProperty(value = "权限名称")
    private String authorityName;

}