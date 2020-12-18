package org.infinity.passport.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
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
@ApiModel("用户权限")
@Document(collection = "UserAuthority")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class UserAuthority extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "用户ID", required = true)
    @NotNull
    @Indexed
    private String userId;

    @ApiModelProperty(value = "权限名称", required = true)
    @NotNull
    private String authorityName;

    public UserAuthority(String userId, String authorityName) {
        super();
        this.userId = userId;
        this.authorityName = authorityName;
    }
}
