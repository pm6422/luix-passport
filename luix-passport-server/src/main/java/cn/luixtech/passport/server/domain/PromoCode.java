package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.idgenerator.TsidGenerator;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class PromoCode {

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 18)
    private String id;

    @NotNull
    @Column(nullable = false, unique = true)
    private String code;

    /**
     * Role to be granted (e.g., ROLE_MEMBER_PRO, ROLE_MEMBER_PREMIUM)
     */
    @NotNull
    @Column(nullable = false)
    private String type;

    @NotNull
    @Column(nullable = false)
    private Integer durationDays;

    @Column(nullable = false)
    private boolean enabled = true;

    private Instant usedAt;

    private String usedBy;

    private Instant expiresAt;

    private String batchId;

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
