package org.infinity.passport.domain;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.MongoOAuth2RefreshTokenDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

import java.io.Serializable;

@Document(collection = "MongoOAuth2RefreshToken")
public class MongoOAuth2RefreshToken extends AbstractAuditableDomain implements Serializable {

    private static final long    serialVersionUID = 1L;

    @Id
    private String               id;

    private String               userName;

    private String               clientId;

    private OAuth2RefreshToken   oAuth2RefreshToken;

    private OAuth2Authentication authentication;

    public MongoOAuth2RefreshToken() {
        super();
    }

    public MongoOAuth2RefreshToken(OAuth2RefreshToken oAuth2RefreshToken, OAuth2Authentication authentication) {
        this.id = oAuth2RefreshToken.getValue();
        this.userName = authentication.getName();
        this.clientId = authentication.getOAuth2Request().getClientId();
        this.oAuth2RefreshToken = oAuth2RefreshToken;
        this.authentication = authentication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
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

    public OAuth2RefreshToken getoAuth2RefreshToken() {
        return oAuth2RefreshToken;
    }

    public void setoAuth2RefreshToken(OAuth2RefreshToken oAuth2RefreshToken) {
        this.oAuth2RefreshToken = oAuth2RefreshToken;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = authentication;
    }

    public MongoOAuth2RefreshTokenDTO asDTO() {
        MongoOAuth2RefreshTokenDTO dest = new MongoOAuth2RefreshTokenDTO();
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

        MongoOAuth2RefreshToken that = (MongoOAuth2RefreshToken) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
