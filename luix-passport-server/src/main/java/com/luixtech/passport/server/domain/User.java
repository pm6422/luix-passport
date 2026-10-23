package com.luixtech.passport.server.domain;

import com.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class User {

    /**
     * Verification code expires in 15 minutes.
     */
    public static final int VERIFICATION_CODE_EXPIRATION_MINUTES = 15;
    /**
     * Reset code expires in 15 minutes.
     */
    public static final int RESET_CODE_EXPIRATION_MINUTES = 15;

    @NotEmpty(message = "username:{Validation.NotEmpty}")
    @Column(nullable = false)
    @Id
    private String username;

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

    private String membershipType;
    private Instant membershipExpiresAt;

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
