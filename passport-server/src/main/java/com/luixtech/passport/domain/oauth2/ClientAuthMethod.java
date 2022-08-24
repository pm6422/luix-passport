package com.luixtech.passport.domain.oauth2;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.IdClass;
import java.io.Serializable;

@Entity
@Data
@IdClass(ClientAuthMethod.ClientAuthenticationMethodId.class)
@NoArgsConstructor
@AllArgsConstructor
public class ClientAuthMethod implements Serializable {
    private static final long   serialVersionUID = -5487974486781192920L;
    @Id
    @Column(name = "client_id", insertable = false, updatable = false)
    private              String clientId;
    @Id
    private              String clientAuthenticationMethod;

    public static ClientAuthMethod of(String clientId, String clientAuthenticationMethod) {
        return new ClientAuthMethod(clientId, clientAuthenticationMethod);
    }

    @Data
    public static class ClientAuthenticationMethodId implements Serializable {
        private static final long   serialVersionUID = -6723254140441855073L;
        private              String clientId;
        private              String clientAuthenticationMethod;
    }

    public ClientAuthenticationMethod toAuthenticationMethod() {
        return new ClientAuthenticationMethod(this.clientAuthenticationMethod);
    }
}
