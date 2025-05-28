package cn.luixtech.passport.server.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.util.Set;

@Data
@NoArgsConstructor
public class AuthUser {
    private String      username;
    private String      email;
    private String      mobileNo;
    private String      firstName;
    private String      lastName;
    private String      locale;
    private String      timeZoneId;
    private String      dateTimeFormatId;
    private String      dateTimeFormat;
    private String      dateFormat;
    private String      timeFormat;
    private Boolean     activated;
    private Boolean     enabled;
    private Set<String> roleIds;
    private Set<String> permissionIds;

    /* <<<<<<<<<<<<<<  ✨ Windsurf Command ⭐ >>>>>>>>>>>>>>>> */

    /**
     * Copies properties from the given ManagedUser to this AuthUser.
     *
     * @param managedUser the source object
     * @return the target object
     */
    /* <<<<<<<<<<  ce56b8f8-f0a9-4335-bf9e-87b1b2520cc9  >>>>>>>>>>> */
    public static AuthUser of(ManagedUser managedUser) {
        AuthUser authUser = new AuthUser();
        BeanUtils.copyProperties(managedUser, authUser);
        return authUser;
    }
}
