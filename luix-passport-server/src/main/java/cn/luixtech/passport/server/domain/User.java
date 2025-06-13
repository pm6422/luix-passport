package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdateDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.Id;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractUpdateDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

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

    private String  firstName;
    private String  lastName;
    @Schema
    @Column(nullable = false)
    private String  passwordHash;
    private String  activationCode;
    private String  verificationCode;
    private Instant verificationCodeSentAt;
    private String  newEmail;
    private String  resetCode;
    private Instant resetAt;
    private String  remark;
    private Boolean activated;
    private Boolean enabled;
    private Instant accountExpiresAt;
    private Instant passwordExpiresAt;
    private Instant lastSignInAt;
    @Schema
    @Column(nullable = false)
    private String  locale;
    @Schema
    @Column(nullable = false)
    private String  dateTimeFormatId;
    @Schema
    @Column(nullable = false)
    private String  timeZoneId;
    private String  source;
}
