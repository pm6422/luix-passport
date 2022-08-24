package com.luixtech.passport.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.domain.base.BaseUser;
import org.springframework.beans.BeanUtils;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Set;

/**
 * A DTO extending the UserDTO, which is meant to be used in the user management UI.
 */
@Schema(description = "extended user DTO")
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
public class ManagedUserDTO extends BaseUser {

    private static final long serialVersionUID        = -8095593058946091229L;
    public static final  int  RAW_PASSWORD_MIN_LENGTH = 5;
    public static final  int  RAW_PASSWORD_MAX_LENGTH = 50;

    @Schema(required = true)
    @NotNull
    @Size(min = RAW_PASSWORD_MIN_LENGTH, max = RAW_PASSWORD_MAX_LENGTH)
    private String password;

    public ManagedUserDTO(User user, Set<String> authorities) {
        BeanUtils.copyProperties(user, this);
        setAuthorities(authorities);
    }

    public User toUser() {
        User user = new User();
        BeanUtils.copyProperties(this, user);
        return user;
    }
}
