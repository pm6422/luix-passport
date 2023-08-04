package com.luixtech.passport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

/**
 * A DTO representing a username and password
 */
@Schema(description = "username and password DTO")
@Data
@Builder
public class UsernameAndPasswordDTO {
    @Schema(description = "username")
    private String username;

    @Schema(description = "new password")
    @NotNull
    @Size(min = ManagedUserDTO.RAW_PASSWORD_MIN_LENGTH, max = ManagedUserDTO.RAW_PASSWORD_MAX_LENGTH)
    private String newPassword;
}
