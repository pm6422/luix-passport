package cn.luixtech.passport.server.config.oauth;

import cn.luixtech.passport.server.config.oauth.jackson.deserializer.AuthUserDeserializer;
import com.fasterxml.jackson.databind.annotation.JsonDeserialize;
import lombok.Getter;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.User;

import java.util.Collection;
import java.util.Set;

@JsonDeserialize(using = AuthUserDeserializer.class)
public class AuthUser extends User {
    private static final long        serialVersionUID = -8021915441738843058L;
    @Getter
    private              String      id;
    @Getter
    private              String      email;
    @Getter
    private              String      mobileNo;
    @Getter
    private              String      firstName;
    @Getter
    private              String      lastName;
    @Getter
    private              String      photoUrl;
    @Getter
    private              String      locale;
    /**
     * Type is String
     */
    @Getter
    private              String      modifiedTime;
    @Getter
    private              Set<String> roles;
    @Getter
    private              Set<String> teamIds;


    public AuthUser(String username, String password, Collection<? extends GrantedAuthority> authorities) {
        super(username, password, authorities);
    }

    public AuthUser(String id, String username, String email, String mobileNo,
                    String firstName, String lastName, String password, boolean enabled,
                    boolean accountNonExpired, boolean credentialsNonExpired, boolean accountNonLocked,
                    String photoUrl, String locale, String modifiedTime,
                    Collection<? extends GrantedAuthority> authorities,
                    Set<String> roles, Set<String> teamIds) {
        super(username, password, enabled, accountNonExpired, credentialsNonExpired, accountNonLocked, authorities);
        this.id = id;
        this.email = email;
        this.mobileNo = mobileNo;
        this.firstName = firstName;
        this.lastName = lastName;
        this.photoUrl = photoUrl;
        this.locale = locale;
        this.modifiedTime = modifiedTime;
        this.roles = roles;
        this.teamIds = teamIds;
    }
}
