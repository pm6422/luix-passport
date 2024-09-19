package cn.luixtech.passport.server.pojo;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;

import java.io.Serializable;

@Schema(description = "user reset DTO")
@Data
public class PasswordRecovery implements Serializable {

    private static final long serialVersionUID = -6442194590613017034L;

    @Schema(description = "reset code")
    @NotEmpty
    private String resetCode;

    @Schema(description = "new raw password")
    @NotEmpty
    @Size(min = ManagedUser.RAW_PASSWORD_MIN_LENGTH, max = ManagedUser.RAW_PASSWORD_MAX_LENGTH)
    private String newRawPassword;

}
