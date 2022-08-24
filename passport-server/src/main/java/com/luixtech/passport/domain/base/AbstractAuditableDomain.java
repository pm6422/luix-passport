package com.luixtech.passport.domain.base;

import com.luixtech.uidgenerator.core.id.IdGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.*;
import java.io.Serializable;
import java.time.Instant;

import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;
import static com.luixtech.passport.config.oauth2.SecurityUtils.getCurrentUsername;
import static com.luixtech.passport.domain.Authority.SYSTEM_ACCOUNT;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
@MappedSuperclass
public abstract class AbstractAuditableDomain implements Serializable {
    private static final long serialVersionUID = -322694592498870599L;

    /**
     * ID should NOT be Long type, because the number which exceeds 16 digits will be display as 0 in front end.
     * e.g. the value 526373442322434543 will be displayed as 526373442322434500 in front end
     * If id is null, save operation equals insert, or else save operation equals update
     */
    @Schema(description = "ID")
    @Id
    protected String id;

    /**
     * Set the proper value when inserting.
     */
    @Schema(description = "creator")
    @Column(updatable = false)
    protected String createdBy;

    /**
     * Set the current time when inserting.
     */
    @Schema(description = "created time")
    @Column(updatable = false)
    protected Instant createdTime;

    /**
     * Set the proper value when updating.
     */
    @Schema(description = "last modifier")
    protected String modifiedBy;

    /**
     * Set the current time when updating.
     */
    @Schema(description = "last modified time")
    protected Instant modifiedTime;

    @PrePersist
    public void prePersist() {
        id = IdGenerator.generateTraceId();
        createdTime = modifiedTime = Instant.now();
        createdBy = modifiedBy = defaultIfEmpty(getCurrentUsername(), SYSTEM_ACCOUNT);
    }

    @PreUpdate
    public void preUpdate() {
        modifiedTime = Instant.now();
        modifiedBy = defaultIfEmpty(getCurrentUsername(), SYSTEM_ACCOUNT);
    }
}
