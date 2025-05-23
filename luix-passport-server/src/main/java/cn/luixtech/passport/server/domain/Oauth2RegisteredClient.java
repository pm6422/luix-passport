package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractAuditableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity(name = "oauth2_registered_client")
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Oauth2RegisteredClient extends AbstractAuditableDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String        clientId;
    private LocalDateTime clientIdIssuedAt;
    private String        clientSecret;
    private LocalDateTime clientSecretExpiresAt;
    private String        clientName;
    private String        clientAuthenticationMethods;
    private String        authorizationGrantTypes;
    private String        redirectUris;
    private String        postLogoutRedirectUris;
    private String        scopes;
    private String        clientSettings;
    private String        tokenSettings;
    private byte[]        photo;
    private Boolean       enabled;
}
