package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "username:{Validation.NotEmpty}")
    @Column(nullable = false)
    @Id
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "email:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String email;

    @Schema
    @Column(nullable = false)
    private String mobileNo;

    private String firstName;
    private String lastName;
    @Schema
    @Column(nullable = false)
    private String passwordHash;
    private String activationCode;
    private String verificationCode;
    private Instant verificationCodeSentAt;
    private String newEmail;
    private String resetCode;
    private Instant resetAt;
    private String remark;
    private Boolean activated;
    private Boolean enabled;
    private Instant accountExpiresAt;
    private Instant passwordExpiresAt;
    private Instant lastSignInAt;
    @Schema
    @Column(nullable = false)
    private String locale;
    @Schema
    @Column(nullable = false)
    private String dateTimeFormatId;
    @Schema
    @Column(nullable = false)
    private String timeZoneId;
    private String source;

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
