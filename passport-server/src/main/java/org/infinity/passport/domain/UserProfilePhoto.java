package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserProfilePhoto extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = -8375847941374800940L;
    @Schema(required = true)
    @NotNull
    @Column(unique = true, nullable = false)
    private              String userId;

    @Schema(required = true)
    @NotNull
    @Basic(fetch = FetchType.LAZY)
    @Lob
    @Column(columnDefinition = "longblob", nullable = false)
    private byte[] profilePhoto;
}
