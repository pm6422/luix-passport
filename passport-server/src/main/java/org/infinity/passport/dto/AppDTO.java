package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.App;

import java.io.Serializable;
import java.util.Set;

/**
 * A DTO representing an App.
 */
@ApiModel("应用DTO")
@Data
@NoArgsConstructor
public class AppDTO extends App implements Serializable {

    private static final long serialVersionUID = 6131756179263179005L;

    @ApiModelProperty(value = "权限名称")
    private Set<String> authorities;

    public AppDTO(String name, Boolean enabled) {
        super();
        setName(name);
        setEnabled(enabled);
    }

    public AppDTO(String name, Boolean enabled, Set<String> authorities) {
        super(name, enabled);
        this.authorities = authorities;
    }
}