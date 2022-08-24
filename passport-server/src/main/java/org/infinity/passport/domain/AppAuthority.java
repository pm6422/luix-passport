package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class AppAuthority extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = 3150836252287314645L;
    @Schema(required = true)
    @NotNull
    private              String appId;

    @Schema(required = true)
    @NotNull
    private String authorityName;

    public AppAuthority(String appId, String authorityName) {
        this.appId = appId;
        this.authorityName = authorityName;
    }
}
