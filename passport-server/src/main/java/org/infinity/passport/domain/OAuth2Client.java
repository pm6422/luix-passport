package org.infinity.passport.domain;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.*;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;
import org.springframework.security.oauth2.core.OAuth2TokenFormat;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.security.oauth2.server.authorization.config.TokenSettings;
import org.springframework.util.StringUtils;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Document
@Data
@NoArgsConstructor
public class OAuth2Client implements Serializable {
    private static final long                 serialVersionUID            = 8481969837769002598L;
    public static final  String               INTERNAL_CLIENT_ID          = "internal-client";
    public static final  String               AUTH_CODE_CLIENT_ID         = "login-client";
    public static final  String               INTERNAL_RAW_CLIENT_SECRET  = "65G-HD9-4PD-j9F-HP5";
    public static final  String               CLIENT_NAME                 = "passport-client";
    @Id
    private              String               id;
    private              String               clientId;
    private              String               clientSecret;
    private              String               clientName;
    private              Instant              clientIdIssuedAt;
    private              Instant              clientSecretExpiresAt;
    private              Set<String>          clientAuthenticationMethods = new HashSet<>();
    private              Set<String>          authorizationGrantTypes     = new HashSet<>();
    private              Set<String>          redirectUris                = new HashSet<>();
    private              Set<OAuth2Scope>     scopes                      = new HashSet<>();
    private              OAuth2ClientSettings oAuth2ClientSettings;
    private              OAuth2TokenSettings  oAuth2TokenSettings;
    @CreatedBy
    protected            String               createdBy;
    @CreatedDate
    protected            Instant              createdTime;
    @LastModifiedBy
    protected            String               modifiedBy;
    @LastModifiedDate
    protected            Instant              modifiedTime;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2Scope implements Serializable {
        private static final long   serialVersionUID = 8481969837769002234L;
        private              String scope;
        private              String description;
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2ClientSettings implements Serializable {
        private static final long    serialVersionUID = -7956711700342643896L;
        private              boolean requireProofKey;
        private              boolean requireAuthorizationConsent;
        private              String  jwkSetUrl;
        private              String  signingAlgorithm;

        /**
         * To client settings.
         *
         * @return the client settings
         */
        public ClientSettings toClientSettings() {
            ClientSettings.Builder builder = ClientSettings.builder()
                    .requireProofKey(this.requireProofKey)
                    .requireAuthorizationConsent(this.requireAuthorizationConsent);
            SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.from(this.signingAlgorithm);
            JwsAlgorithm jwsAlgorithm = signatureAlgorithm == null ? MacAlgorithm.from(this.signingAlgorithm) : signatureAlgorithm;
            if (jwsAlgorithm != null) {
                builder.tokenEndpointAuthenticationSigningAlgorithm(jwsAlgorithm);
            }
            if (StringUtils.hasText(this.jwkSetUrl)) {
                builder.jwkSetUrl(jwkSetUrl);
            }
            return builder.build();
        }

        /**
         * From clientSettings to oauth2ClientSettings.
         *
         * @param clientSettings the clientSettings
         * @return the oauth2ClientSettings
         */
        public static OAuth2ClientSettings fromClientSettings(ClientSettings clientSettings) {
            OAuth2ClientSettings oAuth2ClientSettings = new OAuth2ClientSettings();
            oAuth2ClientSettings.setRequireProofKey(clientSettings.isRequireProofKey());
            oAuth2ClientSettings.setRequireAuthorizationConsent(clientSettings.isRequireAuthorizationConsent());
            oAuth2ClientSettings.setJwkSetUrl(clientSettings.getJwkSetUrl());
            JwsAlgorithm algorithm = clientSettings.getTokenEndpointAuthenticationSigningAlgorithm();
            if (algorithm != null) {
                oAuth2ClientSettings.setSigningAlgorithm(algorithm.getName());
            }
            return oAuth2ClientSettings;
        }
    }

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class OAuth2TokenSettings implements Serializable {
        private static final long     serialVersionUID   = -7077164876986169673L;
        private              Duration accessTokenTimeToLive;
        private              String   tokenFormat;
        private              boolean  reuseRefreshTokens = true;
        private              Duration refreshTokenTimeToLive;
        private              String   idTokenSignatureAlgorithm;

        /**
         * To token settings token settings.
         *
         * @return the token settings
         */
        public TokenSettings toTokenSettings() {

            return TokenSettings.builder()
                    .accessTokenTimeToLive(Optional.ofNullable(this.accessTokenTimeToLive)
                            .orElse(Duration.ofMinutes(5)))
                    .accessTokenFormat(Optional.ofNullable(tokenFormat)
                            .map(OAuth2TokenFormat::new)
                            .orElse(OAuth2TokenFormat.SELF_CONTAINED))
                    .reuseRefreshTokens(this.reuseRefreshTokens)
                    .refreshTokenTimeToLive(Optional.ofNullable(this.refreshTokenTimeToLive)
                            .orElse(Duration.ofMinutes(60)))
                    .idTokenSignatureAlgorithm(Optional.ofNullable(idTokenSignatureAlgorithm)
                            .map(SignatureAlgorithm::from)
                            .orElse(SignatureAlgorithm.RS256))
                    .build();
        }

        /**
         * From tokenSettings to oauth2TokenSettings.
         *
         * @param tokenSettings the tokenSettings
         * @return the oauth2TokenSettings
         */
        public static OAuth2TokenSettings fromTokenSettings(TokenSettings tokenSettings) {
            OAuth2TokenSettings oAuth2TokenSettings = new OAuth2TokenSettings();
            oAuth2TokenSettings.setAccessTokenTimeToLive(tokenSettings.getAccessTokenTimeToLive());
            oAuth2TokenSettings.setTokenFormat(tokenSettings.getAccessTokenFormat().getValue());
            oAuth2TokenSettings.setReuseRefreshTokens(tokenSettings.isReuseRefreshTokens());
            oAuth2TokenSettings.setRefreshTokenTimeToLive(tokenSettings.getRefreshTokenTimeToLive());
            oAuth2TokenSettings.setIdTokenSignatureAlgorithm(tokenSettings.getIdTokenSignatureAlgorithm().getName());
            return oAuth2TokenSettings;
        }
    }

    /**
     * From registeredClient to oauth2Client.
     *
     * @param registeredClient the registeredClient
     * @return the oauth2Client
     */
    public static OAuth2Client fromRegisteredClient(RegisteredClient registeredClient) {
        OAuth2Client oauth2Client = new OAuth2Client();
        oauth2Client.setId(registeredClient.getId());
        oauth2Client.setClientId(registeredClient.getClientId());
        oauth2Client.setClientName(registeredClient.getClientName());
        oauth2Client.setClientIdIssuedAt(registeredClient.getClientIdIssuedAt());
        oauth2Client.setClientSecret(registeredClient.getClientSecret());

        oauth2Client.setClientAuthenticationMethods(registeredClient.getClientAuthenticationMethods()
                .stream()
                .map(ClientAuthenticationMethod::getValue)
                .collect(Collectors.toSet()));
        oauth2Client.setAuthorizationGrantTypes(registeredClient.getAuthorizationGrantTypes()
                .stream()
                .map(AuthorizationGrantType::getValue)
                .collect(Collectors.toSet()));
        oauth2Client.setRedirectUris(registeredClient.getRedirectUris());
        oauth2Client.setScopes(registeredClient.getScopes()
                .stream()
                .filter(scope -> !OidcScopes.OPENID.equals(scope))
                .map(scope -> {
                    OAuth2Scope oauth2Scope = new OAuth2Scope();
                    oauth2Scope.setScope(scope);
                    return oauth2Scope;
                })
                .collect(Collectors.toSet()));
        OAuth2ClientSettings clientSettings = OAuth2ClientSettings.fromClientSettings(registeredClient.getClientSettings());
        oauth2Client.setOAuth2ClientSettings(clientSettings);
        OAuth2TokenSettings tokenSettings = OAuth2TokenSettings.fromTokenSettings(registeredClient.getTokenSettings());
        oauth2Client.setOAuth2TokenSettings(tokenSettings);
        return oauth2Client;
    }

    /**
     * To registered client.
     *
     * @return the registered client
     */
    public RegisteredClient toRegisteredClient() {
        RegisteredClient.Builder builder = RegisteredClient
                .withId(this.id)
                .clientId(this.clientId)
                .clientSecret(this.clientSecret)
                .clientName(this.clientName)
                .clientIdIssuedAt(this.clientIdIssuedAt.atZone(ZoneId.systemDefault()).toInstant())
                .clientSecretExpiresAt(this.clientSecretExpiresAt.atZone(ZoneId.systemDefault()).toInstant())
                .clientAuthenticationMethods(clientAuthenticationMethodSet ->
                        clientAuthenticationMethodSet.addAll(this.clientAuthenticationMethods
                                .stream()
                                .map(ClientAuthenticationMethod::new)
                                .collect(Collectors.toSet())))
                .authorizationGrantTypes(authorizationGrantTypeSet ->
                        authorizationGrantTypeSet.addAll(this.authorizationGrantTypes
                                .stream()
                                .map(AuthorizationGrantType::new)
                                .collect(Collectors.toSet())))
                .redirectUris(redirectUriSet -> redirectUriSet.addAll(this.redirectUris))
                .scopes(scopeSet -> scopeSet.addAll(this.scopes
                        .stream()
                        .map(OAuth2Scope::getScope)
                        .collect(Collectors.toSet())))
                .scope(OidcScopes.OPENID);

        if (this.oAuth2ClientSettings != null) {
            builder.clientSettings(this.oAuth2ClientSettings.toClientSettings());
        }
        if (this.oAuth2TokenSettings != null) {
            builder.tokenSettings(this.oAuth2TokenSettings.toTokenSettings());
        }
        return builder.build();
    }
}

