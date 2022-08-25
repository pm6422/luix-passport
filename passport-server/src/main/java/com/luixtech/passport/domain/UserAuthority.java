package com.luixtech.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import com.luixtech.passport.domain.base.AbstractAuditableDomain;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class UserAuthority extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = 8740966797330793766L;
    @Schema(required = true)
    @NotNull
    @Column(name = "user_id", insertable = false, updatable = false)
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
