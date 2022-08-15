package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the UserAuthority entity.
 */
@Document
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class UserAuthority extends AbstractAuditableDomain implements Serializable {

    private static final long   serialVersionUID = 1L;
    @Schema(required = true)
    @NotNull
    @Indexed
    private              String userId;
    @Schema(required = true)
    @NotNull
    private              String authorityName;

    public UserAuthority(String userId, String authorityName) {
        super();
        this.userId = userId;
        this.authorityName = authorityName;
    }
}
