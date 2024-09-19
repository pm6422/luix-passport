package cn.luixtech.passport.server.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import jakarta.persistence.PrePersist;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serializable;
import java.time.Instant;

import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;
import static org.apache.commons.lang3.StringUtils.defaultIfEmpty;

@Data
@EqualsAndHashCode(callSuper=false)
@MappedSuperclass
public abstract class AbstractCreationDomain extends AbstractBaseDomain implements Serializable {
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
    protected Instant createdAt;

    @PrePersist
    protected void prePersist() {
        super.prePersist();

        createdBy = defaultIfEmpty(getCurrentUsername(), "SYSTEM");
        createdAt = Instant.now();
    }
}
