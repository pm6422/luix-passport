package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.utils.AuthUtils;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Entity(name = "oauth2_registered_client")
@NoArgsConstructor
@AllArgsConstructor
public class Oauth2RegisteredClient {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    private String clientId;
    private Instant clientIdIssuedAt;
    private String clientSecret;
    private Instant clientSecretExpiresAt;
    private String clientName;
    private String clientAuthenticationMethods;
    private String authorizationGrantTypes;
    private String redirectUris;
    private String postLogoutRedirectUris;
    private String scopes;
    private String clientSettings;
    private String tokenSettings;
    private byte[] photo;
    private Boolean enabled;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(updatable = false)
    private String createdBy;

    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        createdBy = AuthUtils.getCurrentUsername();
        updatedBy = createdBy;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = AuthUtils.getCurrentUsername();
    }
}
