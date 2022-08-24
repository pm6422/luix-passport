package org.infinity.passport.domain.oauth2;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;
import java.util.*;
import java.util.stream.Collectors;

@Entity(name = "oauth2_client")
@Data
@NoArgsConstructor
public class OAuth2Client extends AbstractAuditableDomain implements Serializable {
    private static final long                  serialVersionUID            = 8481969837769002598L;
    public static final  String                INTERNAL_CLIENT_ID          = "internal-client";
    public static final  String                AUTH_CODE_CLIENT_ID         = "login-client";
    public static final  String                INTERNAL_RAW_CLIENT_SECRET  = "65G-HD9-4PD-j9F-HP5";
    public static final  String                CLIENT_NAME                 = "passport-client";

    @Column(name = "client_id")
    private              String                clientId;
    private              String                clientSecret;
    private              String                clientName;
    private              Instant               clientIdIssuedAt;
    private              Instant               clientSecretExpiresAt;
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", insertable = false, updatable = false)
    @ToString.Exclude
    private              Set<ClientAuthMethod> clientAuthenticationMethods = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", insertable = false, updatable = false)
    @ToString.Exclude
    private              Set<OAuth2GrantType>  authorizationGrantTypes     = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", insertable = false, updatable = false)
    @ToString.Exclude
    private              Set<RedirectUri>      redirectUris                = new HashSet<>();
    @OneToMany(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", insertable = false, updatable = false)
    @ToString.Exclude
    private              Set<OAuth2Scope>      scopes                      = new HashSet<>();
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    @ToString.Exclude
    private              OAuth2ClientSettings  clientSettings;
    @OneToOne(cascade = CascadeType.ALL, fetch = FetchType.LAZY, orphanRemoval = true)
    @JoinColumn(name = "client_id", referencedColumnName = "client_id", foreignKey = @ForeignKey(ConstraintMode.NO_CONSTRAINT), insertable = false, updatable = false)
    @ToString.Exclude
    private              OAuth2TokenSettings   tokenSettings;

    /**
     * From registeredClient to oauth2Client.
     *
     * @param registeredClient the registeredClient
     * @return the oauth2Client
     */
    public static OAuth2Client fromRegisteredClient(RegisteredClient registeredClient) {
        OAuth2Client oAuth2Client = new OAuth2Client();
        oAuth2Client.setId(registeredClient.getId());
        final String clientId = registeredClient.getClientId();
        oAuth2Client.setClientId(clientId);
        oAuth2Client.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt());
        // Ignore clientSecret clientSecretExpiresAt
        oAuth2Client.setClientSecret(registeredClient.getClientSecret());
        oAuth2Client.setClientName(registeredClient.getClientName());

        oAuth2Client.setClientAuthenticationMethods(registeredClient.getClientAuthenticationMethods()
                .stream()
                .map(method -> {
                    ClientAuthMethod clientAuthMethod = new ClientAuthMethod();
                    clientAuthMethod.setClientId(clientId);
                    clientAuthMethod.setClientAuthenticationMethod(method.getValue());
                    return clientAuthMethod;
                })
                .collect(Collectors.toSet()));
        oAuth2Client.setAuthorizationGrantTypes(registeredClient.getAuthorizationGrantTypes()
                .stream()
                .map(grantType -> {
                    OAuth2GrantType oAuth2GrantType = new OAuth2GrantType();
                    oAuth2GrantType.setClientId(clientId);
                    oAuth2GrantType.setGrantTypeName(grantType.getValue());
                    return oAuth2GrantType;
                })
                .collect(Collectors.toSet()));
        oAuth2Client.setRedirectUris(registeredClient.getRedirectUris().stream()
                .map(redirectUri -> {
                    RedirectUri uri = new RedirectUri();
                    uri.setClientId(clientId);
                    uri.setRedirectUri(redirectUri);
                    return uri;
                })
                .collect(Collectors.toSet()));
        oAuth2Client.setScopes(registeredClient.getScopes()
                .stream()
                .filter(scope -> !OidcScopes.OPENID.equals(scope))
                .map(scope -> {
                    OAuth2Scope oAuth2Scope = new OAuth2Scope();
                    oAuth2Scope.setClientId(clientId);
                    oAuth2Scope.setScope(scope);
                    return oAuth2Scope;
                })
                .collect(Collectors.toSet()));
        OAuth2ClientSettings settings = OAuth2ClientSettings.fromClientSettings(registeredClient.getClientSettings());
        settings.setClientId(clientId);
        oAuth2Client.setClientSettings(settings);
        OAuth2TokenSettings oAuth2TokenSettings = OAuth2TokenSettings.fromTokenSettings(registeredClient.getTokenSettings());
        oAuth2TokenSettings.setClientId(clientId);
        oAuth2Client.setTokenSettings(oAuth2TokenSettings);
        return oAuth2Client;
    }

    /**
     * To registered client.
     *
     * @return the registered client
     */
    public RegisteredClient toRegisteredClient() {
        Set<ClientAuthMethod> clientAuthMethods = clientAuthenticationMethods == null ? Collections.emptySet() : clientAuthenticationMethods;
        Set<OAuth2GrantType> oAuth2GrantTypes = authorizationGrantTypes == null ? Collections.emptySet() : authorizationGrantTypes;
        Set<RedirectUri> uris = redirectUris == null ? Collections.emptySet() : redirectUris;
        Set<OAuth2Scope> oAuth2Scopes = scopes == null ? Collections.emptySet() : scopes;

        RegisteredClient.Builder builder = RegisteredClient.withId(Optional.ofNullable(this.id).orElse(UUID.randomUUID().toString()))
                .clientId(Optional.ofNullable(this.clientId).orElse(UUID.randomUUID().toString()))
                .clientSecret(this.clientSecret)
                .clientIdIssuedAt(this.clientIdIssuedAt)
                .clientSecretExpiresAt(this.clientSecretExpiresAt)
                .clientName(this.clientName)
                .clientAuthenticationMethods(clientAuthenticationMethodSet ->
                        clientAuthenticationMethodSet.addAll(clientAuthMethods.stream()
                                .map(ClientAuthMethod::toAuthenticationMethod)
                                .collect(Collectors.toSet())))
                .authorizationGrantTypes(authorizationGrantTypeSet ->
                        authorizationGrantTypeSet.addAll(oAuth2GrantTypes.stream()
                                .map(OAuth2GrantType::toGrantType)
                                .collect(Collectors.toSet())))
                .redirectUris(redirectUriSet -> redirectUriSet.addAll(uris.stream()
                        .map(RedirectUri::getRedirectUri)
                        .collect(Collectors.toSet())))
                .scopes(scopeSet -> scopeSet.addAll(oAuth2Scopes.stream()
                        .map(OAuth2Scope::getScope)
                        .collect(Collectors.toSet())))
                .scope(OidcScopes.OPENID)
                .clientSettings(this.clientSettings.toClientSettings())
                .tokenSettings(this.tokenSettings.toTokenSettings());
        return builder.build();
    }
}

