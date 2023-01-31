package com.luixtech.passport.domain;

import com.luixtech.passport.domain.base.AbstractAuditableDomain;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
public class AuthorityMenu extends AbstractAuditableDomain implements Serializable {
    private static final long   serialVersionUID = -903904401017894767L;
    @NotNull
    private              String authorityName;
    @NotNull
    private              String menuId;

    public AuthorityMenu(String authorityName, String menuId) {
        super();
        this.authorityName = authorityName;
        this.menuId = menuId;
    }
}