package com.luixtech.passport.domain.oauth2;

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
@IdClass(RedirectUri.RedirectUriId.class)
@NoArgsConstructor
@AllArgsConstructor
public class RedirectUri implements Serializable {
    private static final long   serialVersionUID = 4206129330992904722L;
    @Id
    @Column(insertable = false, updatable = false)
    private              String clientId;
    @Id
    private              String redirectUri;

    public static RedirectUri of(String clientId, String redirectUri) {
        return new RedirectUri(clientId, redirectUri);
    }

    @Data
    public static class RedirectUriId implements Serializable {
        private static final long   serialVersionUID = 8954381042745152023L;
        private              String clientId;
        private              String redirectUri;
    }
}
