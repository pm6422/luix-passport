package com.luixtech.passport.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;

import javax.persistence.Column;
import javax.persistence.MappedSuperclass;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import java.io.Serializable;
import java.time.Instant;

import static com.luixtech.passport.config.oauth2.SecurityUtils.getCurrentUsername;
import static com.luixtech.passport.domain.Authority.SYSTEM_ACCOUNT;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
@MappedSuperclass
public abstract class AbstractAuditableDomain extends BaseDomain implements Serializable {
    private static final long serialVersionUID = -322694592498870599L;

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
        super.prePersist();
        createdTime = modifiedTime = Instant.now();
        createdBy = modifiedBy = defaultIfEmpty(getCurrentUsername(), SYSTEM_ACCOUNT);
    }

    @PreUpdate
    public void preUpdate() {
        modifiedTime = Instant.now();
        modifiedBy = defaultIfEmpty(getCurrentUsername(), SYSTEM_ACCOUNT);
    }
}
