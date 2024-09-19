package cn.luixtech.passport.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
@NoArgsConstructor
public class AuthUser {
    private String      id;
    private String      username;
    private String      email;
    private String      mobileNo;
    private String      firstName;
    private String      lastName;
    private String      locale;
    private String      dateTimeFormat;
    private String      timeZone;
    private Boolean     activated;
    private Boolean     enabled;
    private Set<String> roles;

    public static AuthUser of(ManagedUser managedUser) {
        AuthUser authUser = new AuthUser();
        BeanUtils.copyProperties(managedUser, authUser);
        return authUser;
    }
}
