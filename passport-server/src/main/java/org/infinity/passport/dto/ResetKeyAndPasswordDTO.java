package org.infinity.passport.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("重置密码DTO")
public class ResetKeyAndPasswordDTO implements Serializable {

    private static final long serialVersionUID = -6442194590613017034L;

    @ApiModelProperty("重置码")
    @NotNull
    private String            key;

    @ApiModelProperty("新密码")
    @NotNull
    @Size(min = ManagedUserDTO.RAW_PASSWORD_MIN_LENGTH, max = ManagedUserDTO.RAW_PASSWORD_MAX_LENGTH)
    private String            newPassword;

    public ResetKeyAndPasswordDTO() {
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getNewPassword() {
        return newPassword;
    }

    public void setNewPassword(String newPassword) {
        this.newPassword = newPassword;
    }

    @Override
    public String toString() {
        return "ResetKeyAndPasswordDTO [key=" + key + ", newPassword=" + newPassword + "]";
    }
}
