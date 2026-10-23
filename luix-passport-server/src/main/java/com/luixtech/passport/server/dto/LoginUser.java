package com.luixtech.passport.server.dto;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
public class LoginUser {
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
    private Instant     signInAt;
    private String      membershipType;
    private Instant     membershipExpiresAt;

    /**
     * Copies properties from the given ManagedUser to this AuthUser.
     *
     * @param managedUser the source object
     * @return the target object
     */
    public static LoginUser of(ManagedUser managedUser) {
        LoginUser loginUser = new LoginUser();
        BeanUtils.copyProperties(managedUser, loginUser);
        return loginUser;
    }
}
