package cn.luixtech.passport.server.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
@EqualsAndHashCode(callSuper = true)
@MappedSuperclass
public abstract class AbstractUpdateDomain extends AbstractCreationDomain implements Serializable {
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
    public void prePersist() {
        super.prePersist();

        modifiedAt = createdAt;
        modifiedBy = createdBy;
    }

    public void preUpdate() {
        modifiedAt = Instant.now();
        modifiedBy = getCurrentUsername();
    }
}
