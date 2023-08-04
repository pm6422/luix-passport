package com.luixtech.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@Data
@IdClass(UserAuthority.UserAuthorityId.class)
@NoArgsConstructor
@AllArgsConstructor
public class UserAuthority implements Serializable {
    private static final long   serialVersionUID = 8740966797330793766L;
    @Schema(required = true)
    @Column(name = "user_id", updatable = false)
    @Id
    private              String userId;
    @Schema(required = true)
    @Id
    private              String authorityName;

    @Data
    public static class UserAuthorityId implements Serializable {
        private static final long   serialVersionUID = -6855173161392372070L;
        private              String userId;
        private              String authorityName;
    }
}
