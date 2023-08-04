package com.luixtech.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import javax.validation.constraints.NotNull;
import java.io.Serializable;

@Entity
@Data
@IdClass(AppAuthority.AppAuthorityId.class)
@NoArgsConstructor
@AllArgsConstructor
public class AppAuthority implements Serializable {
    private static final long   serialVersionUID = 3150836252287314645L;
    @Schema(required = true)
    @NotNull
    @Id
    @Column(name = "app_id", insertable = false, updatable = false)
    private              String appId;
    @Schema(required = true)
    @NotNull
    @Id
    private              String authorityName;

    @Data
    public static class AppAuthorityId implements Serializable {
        private static final long   serialVersionUID = -6855173161392372070L;
        private              String appId;
        private              String authorityName;
    }
}
