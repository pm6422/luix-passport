package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO representing a username and password
 */
@ApiModel("用户名密码DTO")
@Data
@Builder
public class UserNameAndPasswordDTO {
    @ApiModelProperty("用户名")
    private String userName;

    @ApiModelProperty("新密码")
    @NotNull
    @Size(min = ManagedUserDTO.RAW_PASSWORD_MIN_LENGTH, max = ManagedUserDTO.RAW_PASSWORD_MAX_LENGTH)
    private String newPassword;
}
