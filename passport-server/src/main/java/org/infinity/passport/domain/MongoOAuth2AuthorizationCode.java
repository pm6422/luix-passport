package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;

@Schema(description = "单点登录授权码信息")
@Document(collection = "MongoOAuth2AuthorizationCode")
@Data
@NoArgsConstructor
public class MongoOAuth2AuthorizationCode implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String code;

    private OAuth2Authentication authentication;


    @PersistenceConstructor
    public MongoOAuth2AuthorizationCode(String code, OAuth2Authentication authentication) {
        this.code = code;
        this.authentication = authentication;
    }
}
