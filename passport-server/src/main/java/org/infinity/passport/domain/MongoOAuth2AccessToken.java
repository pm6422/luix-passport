package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.MongoOAuth2AccessTokenDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;
import java.util.Date;

@Document(collection = "MongoOAuth2AccessToken")
@Data
@EqualsAndHashCode(callSuper = true)
public class MongoOAuth2AccessToken extends AbstractAuditableDomain implements Serializable {

    private static final long                 serialVersionUID = 1L;
    @Id
    private              String               id;                   // AccessTokenId存储在id字段则保证所有客户端共享，一个客户端退出可以退出所有客户端
    private              OAuth2AccessToken    oAuth2AccessToken;
    private              String               authenticationId;
    private              String               userName;
    private              String               clientId;
    @Indexed(expireAfterSeconds = 0)//Expire Documents at a Specific Clock Time
    private              Date                 expiration;
    private              OAuth2Authentication authentication;
    private              String               refreshToken;

    public MongoOAuth2AccessToken() {
        super();
    }

    @PersistenceConstructor
    public MongoOAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken, OAuth2Authentication authentication,
                                  String authenticationId) {
        this.id = oAuth2AccessToken.getValue();
        this.oAuth2AccessToken = oAuth2AccessToken;
        this.authenticationId = authenticationId;
        this.userName = authentication.getName();
        this.clientId = authentication.getOAuth2Request().getClientId();
        this.expiration = oAuth2AccessToken.getExpiration();

        this.authentication = authentication;
        if (oAuth2AccessToken.getRefreshToken() != null) {
            this.refreshToken = oAuth2AccessToken.getRefreshToken().getValue();
        }
    }

    public MongoOAuth2AccessTokenDTO asDTO() {
        MongoOAuth2AccessTokenDTO dest = new MongoOAuth2AccessTokenDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }
}
