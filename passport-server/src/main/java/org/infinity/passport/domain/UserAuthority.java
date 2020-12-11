package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the UserAuthority entity.
 */
@Document(collection = "UserAuthority")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class UserAuthority extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    private String userId;

    @NotNull
    private String authorityName;

    public UserAuthority(String userId, String authorityName) {
        super();
        this.userId = userId;
        this.authorityName = authorityName;
    }
}
