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
public class Permission {

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String id;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "resourceType:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String resourceType;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "action:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String action;

    private String remark;

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
