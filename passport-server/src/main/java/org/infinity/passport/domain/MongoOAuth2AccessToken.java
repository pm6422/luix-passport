package org.infinity.passport.domain;

import java.io.Serializable;
import java.util.Date;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.MongoOAuth2AccessTokenDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Document
public class MongoOAuth2AccessToken extends AbstractAuditableDomain implements Serializable {

    private static final long    serialVersionUID = 1L;

    @Id
    private String               id;                   // AccessTokenId存储在id字段则保证所有客户端共享，一个客户端退出可以退出所有客户端

    private OAuth2AccessToken    oAuth2AccessToken;

    private String               authenticationId;

    private String               userName;

    private String               clientId;

    private Date                 expiration;

    private OAuth2Authentication authentication;

    private String               refreshToken;

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

    public String getId() {
        return id;
    }

    public void setId(String tokenId) {
        this.id = tokenId;
    }

    public OAuth2AccessToken getoAuth2AccessToken() {
        return oAuth2AccessToken;
    }

    public void setoAuth2AccessToken(OAuth2AccessToken oAuth2AccessToken) {
        this.oAuth2AccessToken = oAuth2AccessToken;
    }

    public String getAuthenticationId() {
        return authenticationId;
    }

    public void setAuthenticationId(String authenticationId) {
        this.authenticationId = authenticationId;
    }

    public String getUserName() {
        return userName;
    }

    public void setUserName(String userName) {
        this.userName = userName;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public Date getExpiration() {
        return expiration;
    }

    public void setExpiration(Date expiration) {
        this.expiration = expiration;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = authentication;
    }

    public String getRefreshToken() {
        return refreshToken;
    }

    public void setRefreshToken(String refreshToken) {
        this.refreshToken = refreshToken;
    }

    public MongoOAuth2AccessTokenDTO asDTO() {
        MongoOAuth2AccessTokenDTO dest = new MongoOAuth2AccessTokenDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MongoOAuth2AccessToken that = (MongoOAuth2AccessToken) o;

        if (id != null ? !id.equals(that.id) : that.id != null) {
            return false;
        }

        return true;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
