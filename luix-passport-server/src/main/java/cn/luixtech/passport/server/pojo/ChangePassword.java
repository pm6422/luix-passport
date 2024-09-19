package cn.luixtech.passport.server.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Builder;
import lombok.Data;


/**
 * A DTO representing a username and password
 */
@Schema(description = "username and password DTO")
@Data
@Builder
public class ChangePassword {
    @Schema(description = "old password")
    @NotEmpty
    @Size(min = ManagedUser.RAW_PASSWORD_MIN_LENGTH, max = ManagedUser.RAW_PASSWORD_MAX_LENGTH)
    private String oldRawPassword;

    @Schema(description = "new password")
    @NotEmpty
    @Size(min = ManagedUser.RAW_PASSWORD_MIN_LENGTH, max = ManagedUser.RAW_PASSWORD_MAX_LENGTH)
    private String newRawPassword;

    @Schema(description = "verification code")
    @NotEmpty
    private String verificationCode;
}
