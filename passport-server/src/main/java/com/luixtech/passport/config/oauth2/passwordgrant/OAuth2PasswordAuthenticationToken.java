package com.luixtech.passport.config.oauth2.passwordgrant;

import org.springframework.lang.Nullable;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.endpoint.OAuth2ParameterNames;
import org.springframework.security.oauth2.server.authorization.authentication.OAuth2AuthorizationGrantAuthenticationToken;

import java.io.Serializable;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class OAuth2PasswordAuthenticationToken extends OAuth2AuthorizationGrantAuthenticationToken implements Serializable {
    private static final long        serialVersionUID = -7793318818051403023L;
    private final        Set<String> scopes;

    /**
     * Constructs an {@code OAuth2ClientCredentialsAuthenticationToken} using the provided parameters.
     *
     * @param clientPrincipal      the authenticated client principal
     * @param scopes               the requested scope(s)
     * @param additionalParameters the additional parameters
     */
    public OAuth2PasswordAuthenticationToken(Authentication clientPrincipal,
                                             @Nullable Set<String> scopes,
                                             @Nullable Map<String, Object> additionalParameters) {
        super(AuthorizationGrantType.PASSWORD, clientPrincipal, additionalParameters);
        this.scopes = Collections.unmodifiableSet(scopes != null ? new HashSet<>(scopes) : Collections.emptySet());
    }

    /**
     * Returns the requested scope(s).
     *
     * @return the requested scope(s), or an empty {@code Set} if not available
     */
    public Set<String> getScopes() {
        return this.scopes;
    }

    @Override
    public Object getCredentials() {
        return this.getAdditionalParameters().get(OAuth2ParameterNames.PASSWORD);
    }
}
