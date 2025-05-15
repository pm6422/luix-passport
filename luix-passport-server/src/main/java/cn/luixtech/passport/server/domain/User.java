package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractAuditableDomain;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class User extends AbstractAuditableDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "username:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "email:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String email;

    @Schema
    @Column(nullable = false)
    private String mobileNo;

    private String        firstName;
    private String        lastName;
    private String        passwordHash;
    private String        activationCode;
    private String        verificationCode;
    private LocalDateTime verificationCodeSentAt;
    private String        newEmail;
    private String        resetCode;
    private LocalDateTime resetAt;
    private String        remark;
    private Boolean       activated;
    private Boolean       enabled;
    private LocalDateTime accountExpiresAt;
    private LocalDateTime passwordExpiresAt;
    private LocalDateTime lastSignInAt;
    private String        locale;
    private String        dateTimeFormatId;
    private String        timeZoneId;
}
