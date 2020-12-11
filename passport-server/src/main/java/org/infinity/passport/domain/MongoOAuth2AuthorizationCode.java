package org.infinity.passport.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.infinity.passport.dto.MongoOAuth2AuthorizationCodeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;

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

    public MongoOAuth2AuthorizationCodeDTO toDTO() {
        MongoOAuth2AuthorizationCodeDTO dest = new MongoOAuth2AuthorizationCodeDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }
}
