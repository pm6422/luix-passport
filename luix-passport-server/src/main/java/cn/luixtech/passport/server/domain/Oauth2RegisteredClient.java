package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity(name = "oauth2_registered_client")
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Oauth2RegisteredClient extends AbstractUpdatableDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String  clientId;
    private Instant clientIdIssuedAt;
    private String  clientSecret;
    private Instant clientSecretExpiresAt;
    private String  clientName;
    private String  clientAuthenticationMethods;
    private String  authorizationGrantTypes;
    private String  redirectUris;
    private String  postLogoutRedirectUris;
    private String  scopes;
    private String  clientSettings;
    private String  tokenSettings;
    private byte[]  photo;
    private Boolean enabled;
}
