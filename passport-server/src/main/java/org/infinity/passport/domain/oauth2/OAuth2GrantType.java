package org.infinity.passport.domain.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.AuthorizationGrantType;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity(name = "oauth2_grant_type")
@Data
@IdClass(OAuth2GrantType.GrantTypeId.class)
@NoArgsConstructor
@AllArgsConstructor
public class OAuth2GrantType implements Serializable {
    private static final long   serialVersionUID = 6668862096580386130L;
    @Id
    @Column(name = "client_id", insertable = false, updatable = false)
    private              String clientId;
    @Id
    private              String grantTypeName;

    public static OAuth2GrantType of(String clientId, String grantTypeName) {
        return new OAuth2GrantType(clientId, grantTypeName);
    }

    /**
     * The type Grant type id.
     */
    @Data
    public static class GrantTypeId implements Serializable {
        private static final long   serialVersionUID = 4564929425041585437L;
        private              String clientId;
        private              String grantTypeName;
    }

    /**
     * To grant type authorization grant type.
     *
     * @return the authorization grant type
     */
    public AuthorizationGrantType toGrantType() {
        return new AuthorizationGrantType(grantTypeName);
    }
}
