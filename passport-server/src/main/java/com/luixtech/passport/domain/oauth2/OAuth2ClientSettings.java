package com.luixtech.passport.domain.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.jose.jws.JwsAlgorithm;
import org.springframework.security.oauth2.jose.jws.MacAlgorithm;
import org.springframework.security.oauth2.jose.jws.SignatureAlgorithm;
import org.springframework.security.oauth2.server.authorization.config.ClientSettings;
import org.springframework.util.StringUtils;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import java.io.Serializable;

@Entity(name = "oauth2_client_settings")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2ClientSettings implements Serializable {
    private static final long    serialVersionUID = -5191372174843198204L;
    @Id
    @Column(name = "client_id", insertable = false, updatable = false)
    private              String  clientId;
    private              boolean requireProofKey;
    private              boolean requireAuthorizationConsent;
    private              String  jwkSetUrl;
    private              String  signingAlgorithm;

    public static OAuth2ClientSettings of(String clientId, boolean requireProofKey,
                                          boolean requireAuthorizationConsent,
                                          String jwkSetUrl, String signingAlgorithm) {
        return new OAuth2ClientSettings(clientId, requireProofKey, requireAuthorizationConsent, jwkSetUrl, signingAlgorithm);
    }

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
        JwsAlgorithm jwsAlgorithm = signatureAlgorithm == null
                ? MacAlgorithm.from(this.signingAlgorithm) : signatureAlgorithm;
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
