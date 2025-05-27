package cn.luixtech.passport.server.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;

@Data
@MappedSuperclass
public abstract class AbstractCreationDomain implements Serializable {
    @Serial
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

    public void prePersist() {
        createdBy = getCurrentUsername();
        createdAt = Instant.now();
    }
}
