package com.luixtech.passport.server.domain;

import com.luixtech.passport.server.utils.AuthUtils;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import com.luixtech.springbootframework.idgenerator.TsidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class DataDict {
    public static final String CATEGORY_CODE_TIMEZONE = "Timezone";

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 18)
    private String id;

    @NotEmpty(message = "categoryCode:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String categoryCode;

    @NotEmpty(message = "dictCode:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String dictCode;

    private String dictName;

    private String remark;

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
