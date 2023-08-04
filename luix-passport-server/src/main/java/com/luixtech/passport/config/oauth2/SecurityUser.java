package com.luixtech.passport.config.oauth2;

import lombok.Getter;
import lombok.Setter;
import lombok.ToString;
import org.springframework.security.core.GrantedAuthority;

import java.util.Collection;

@ToString
public class SecurityUser extends org.springframework.security.core.userdetails.User {
    private static final long   serialVersionUID = -8021915441738843058L;
    @Setter
    @Getter
    private              String id;
    @Setter
    @Getter
    private              String firstName;
    @Setter
    @Getter
    private              String lastName;

    public SecurityUser(String id, String username, String firstName, String lastName,
                        String password, boolean enabled,
                        boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                        Collection<? extends GrantedAuthority> authorities) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.firstName = firstName;
        this.lastName = lastName;
    }
}
