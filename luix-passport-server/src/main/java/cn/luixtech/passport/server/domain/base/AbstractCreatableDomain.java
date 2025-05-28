package cn.luixtech.passport.server.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;

@Data
@EqualsAndHashCode(callSuper=false)
@MappedSuperclass
public abstract class AbstractCreatableDomain extends AbstractBaseDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = -322694592498870599L;

    /**
     * Set the current time when inserting.
     */
    @Schema(description = "created time")
    @Column(updatable = false)
    protected Instant createdAt;

    /**
     * Set the proper value when inserting.
     */
    @Schema(description = "creator")
    @Column(updatable = false)
    protected String createdBy;

    @Override
    public void prePersist() {
        super.prePersist();

        createdAt = Instant.now();
        createdBy = getCurrentUsername();
    }
}
