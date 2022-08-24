package com.luixtech.passport.domain.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity(name = "oauth2_scope")
@Data
@IdClass(OAuth2Scope.OAuth2ScopeId.class)
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2Scope implements Serializable {
    private static final long   serialVersionUID = -706216766442096613L;
    @Id
    @Column(name = "client_id", insertable = false, updatable = false)
    private              String clientId;
    @Id
    private              String scope;
    private              String description;

    public static OAuth2Scope of(String clientId, String scope, String description) {
        return new OAuth2Scope(clientId, scope, description);
    }

    @Data
    public static class OAuth2ScopeId implements Serializable {
        private static final long   serialVersionUID = 5069375042974832320L;
        private              String clientId;
        private              String scope;
    }
}
