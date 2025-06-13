package cn.luixtech.passport.server.config.oauth;

import lombok.Getter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.oauth2.core.user.OAuth2User;

public class HybridAuthenticationToken extends AbstractAuthenticationToken {
    @Getter
    private final OAuth2User oauth2User;
    private final AuthUser   localUser;
    @Getter
    private final String     clientRegistrationId;

    public HybridAuthenticationToken(OAuth2User oauth2User, AuthUser localUser, String clientRegistrationId) {
        super(localUser.getAuthorities());
        this.oauth2User = oauth2User;
        this.localUser = localUser;
        this.clientRegistrationId = clientRegistrationId;

        setAuthenticated(true);
    }

    @Override
    public Object getPrincipal() {
        return localUser;
    }

    @Override
    public Object getCredentials() {
        return oauth2User;
    }
}