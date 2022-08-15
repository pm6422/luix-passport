package org.infinity.passport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@Schema(description = "user reset DTO")
@Data
public class ResetKeyAndPasswordDTO implements Serializable {

    private static final long serialVersionUID = -6442194590613017034L;

    @Schema(description = "reset code")
    @NotNull
    private String key;

    @Schema(description = "new password")
    @NotNull
    @Size(min = ManagedUserDTO.RAW_PASSWORD_MIN_LENGTH, max = ManagedUserDTO.RAW_PASSWORD_MAX_LENGTH)
    private String newPassword;

}
