package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotEmpty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserRole extends AbstractUpdatableDomain implements Serializable {

    public static final String ROLE_ANONYMOUS = "ROLE_ANONYMOUS";
    public static final String ROLE_USER      = "ROLE_USER";
    public static final String ROLE_ADMIN     = "ROLE_ADMIN";
    public static final String ROLE_DEVELOPER = "ROLE_DEVELOPER";
    
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "username:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String username;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "roleId:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String roleId;
}
