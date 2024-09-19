package cn.luixtech.passport.server.pojo;

import cn.luixtech.passport.server.domain.Oauth2RegisteredClient;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.settings.ClientSettings;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;
import java.time.ZoneId;
import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.BCRYPT_PASSWORD_ENCODER;
import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.DEFAULT_PASSWORD_ENCODER_PREFIX;

@Data
@NoArgsConstructor
public class Oauth2Client implements Serializable {
    @Serial
    private static final long    serialVersionUID           = 8481969837769002598L;
    public static final  String  INTERNAL_CLIENT_ID         = "internal-client";
    public static final  String  AUTH_CODE_CLIENT_ID        = "login-client";
    public static final  String  INTERNAL_RAW_CLIENT_SECRET = "65G-HD9-4PD-j9F-HP5";
    public static final  String  CLIENT_NAME                = "passport-client";
    private              String  id;
    private              String  clientId;
    private              String  clientName;
    private              String  rawClientSecret;
    private              String  clientSecret;
    private              Instant clientIdIssuedAt;
    private              Instant clientSecretExpiresAt;
    private              Integer validityInDays;
    private              Boolean enabled;
    private              byte[]  photo;

    private Set<String> clientAuthenticationMethods = new HashSet<>();
    private Set<String> authorizationGrantTypes     = new HashSet<>();
    private Set<String> redirectUris                = new HashSet<>();
    private Set<String> postLogoutRedirectUris      = new HashSet<>();
    private Set<String> scopes                      = new HashSet<>();


    public void setClientAuthenticationMethods(Set<String> clientAuthenticationMethods) {
        if (this.clientAuthenticationMethods == null) {
            this.clientAuthenticationMethods = clientAuthenticationMethods;
        } else {
            this.clientAuthenticationMethods.clear();
            this.clientAuthenticationMethods.addAll(clientAuthenticationMethods);
        }
    }

    public void setAuthorizationGrantTypes(Set<String> authorizationGrantTypes) {
        if (this.authorizationGrantTypes == null) {
            this.authorizationGrantTypes = authorizationGrantTypes;
        } else {
            this.authorizationGrantTypes.clear();
            this.authorizationGrantTypes.addAll(authorizationGrantTypes);
        }
    }

    public void setRedirectUris(Set<String> redirectUris) {
        if (this.redirectUris == null) {
            this.redirectUris = redirectUris;
        } else {
            this.redirectUris.clear();
            this.redirectUris.addAll(redirectUris);
        }
    }

    public void setPostLogoutRedirectUris(Set<String> postLogoutRedirectUris) {
        if (this.postLogoutRedirectUris == null) {
            this.postLogoutRedirectUris = postLogoutRedirectUris;
        } else {
            this.postLogoutRedirectUris.clear();
            this.postLogoutRedirectUris.addAll(postLogoutRedirectUris);
        }
    }

    public void setScopes(Set<String> scopes) {
        if (this.scopes == null) {
            this.scopes = scopes;
        } else {
            this.scopes.clear();
            this.scopes.addAll(scopes);
        }
    }

    /**
     * To registered client.
     *
     * @return the registered client
     */
    public RegisteredClient toRegisteredClient() {
        String clientSecret = StringUtils.defaultIfEmpty(this.clientSecret, DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(this.getRawClientSecret()));
        return RegisteredClient
                .withId(Optional.ofNullable(this.id).orElse(String.valueOf(IdGenerator.generateShortId())))
                .clientId(Optional.ofNullable(this.clientId).orElse(IdGenerator.generateId()))
                .clientName(this.clientName)
                .clientSecret(clientSecret)
                .clientIdIssuedAt(this.clientIdIssuedAt)
                .clientAuthenticationMethods(clientAuthenticationMethodSet ->
                        clientAuthenticationMethodSet.addAll(clientAuthenticationMethods.stream()
                                .map(ClientAuthenticationMethod::new)
                                .collect(Collectors.toSet())))
                .authorizationGrantTypes(authorizationGrantTypeSet ->
                        authorizationGrantTypeSet.addAll(authorizationGrantTypes.stream()
                                .map(AuthorizationGrantType::new)
                                .collect(Collectors.toSet())))
                .redirectUris(redirectUriSet -> redirectUriSet.addAll(redirectUris))
                .postLogoutRedirectUris(postLogoutRedirectUris -> postLogoutRedirectUris.addAll(this.postLogoutRedirectUris))
                .scopes(scopeSet -> scopeSet.addAll(scopes))
                // add openid scope as default
                .scope(OidcScopes.OPENID)
                .clientSecretExpiresAt(this.clientSecretExpiresAt)
                .clientSettings(ClientSettings.builder().requireAuthorizationConsent(true).build())
                .build();
    }

    /**
     * From registeredClient to oauth2Client.
     *
     * @param registeredClient the registeredClient
     * @return the oauth2Client
     */
    public static Oauth2Client fromRegisteredClient(Oauth2RegisteredClient registeredClient) {
        Oauth2Client oauth2Client = new Oauth2Client();
        oauth2Client.setId(registeredClient.getId());
        oauth2Client.setClientId(registeredClient.getClientId());
        oauth2Client.setClientName(registeredClient.getClientName());
        oauth2Client.setRawClientSecret(StringUtils.EMPTY);
        oauth2Client.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt().atZone(ZoneId.systemDefault()).toInstant());
        oauth2Client.setClientAuthenticationMethods(Arrays.stream(registeredClient.getClientAuthenticationMethods().split(",")).collect(Collectors.toSet()));
        oauth2Client.setAuthorizationGrantTypes(Arrays.stream(registeredClient.getAuthorizationGrantTypes().split(",")).collect(Collectors.toSet()));
        if(StringUtils.isNotEmpty(registeredClient.getRedirectUris())) {
            oauth2Client.setRedirectUris(new HashSet<>(Arrays.asList(registeredClient.getRedirectUris().split(","))));
        }
        if (StringUtils.isNotEmpty(registeredClient.getPostLogoutRedirectUris())) {
            oauth2Client.setPostLogoutRedirectUris(Stream.of(registeredClient.getPostLogoutRedirectUris()).collect(Collectors.toSet()));
        }
        oauth2Client.setScopes(Arrays.stream(registeredClient.getScopes().split(",")).collect(Collectors.toSet()));
        if (registeredClient.getClientSecretExpiresAt() != null) {
            oauth2Client.setClientSecretExpiresAt(registeredClient.getClientSecretExpiresAt().atZone(ZoneId.systemDefault()).toInstant());
        }
        oauth2Client.setEnabled(registeredClient.getEnabled());
        return oauth2Client;
    }
}

