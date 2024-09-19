package cn.luixtech.passport.server.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class AbstractAuditableDomain extends AbstractCreationDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = -322694592498870599L;

    /**
     * Set the proper value when inserting or updating.
     */
    @Schema(description = "last modifier")
    protected String modifiedBy;

    /**
     * Set the current time when inserting or updating.
     */
    @Schema(description = "last modified time")
    protected Instant modifiedAt;

    @Override
    @PrePersist
    protected void prePersist() {
        super.prePersist();

        modifiedAt = createdAt;
        modifiedBy = createdBy;
    }

    @PreUpdate
    protected void preUpdate() {
        modifiedAt = Instant.now();
        modifiedBy = defaultIfEmpty(getCurrentUsername(), "SYSTEM");
    }
}
