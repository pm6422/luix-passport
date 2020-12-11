package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO representing an App.
 */
@ApiModel("应用DTO")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AppDTO implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    @ApiModelProperty(value = "应用名称")
    private String name;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

    @ApiModelProperty(value = "权限名称")
    private Set<String> authorities;

    public AppDTO(String name, Boolean enabled) {
        super();
        this.name = name;
        this.enabled = enabled;
    }
}