package cn.luixtech.passport.server.pojo;


import cn.luixtech.passport.server.domain.User;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.io.Serial;
import java.util.Set;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management UI.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ManagedUser extends User {

    @Serial
    private static final long serialVersionUID        = -8095593058946091229L;
    public static final  int  RAW_PASSWORD_MIN_LENGTH = 5;
    public static final  int  RAW_PASSWORD_MAX_LENGTH = 10;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @Size(min = RAW_PASSWORD_MIN_LENGTH, max = RAW_PASSWORD_MAX_LENGTH)
    private String password;

    private Set<String> roleIds;

    private Set<String> permissionIds;

    private String locale;

    private String timeZoneId;

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
