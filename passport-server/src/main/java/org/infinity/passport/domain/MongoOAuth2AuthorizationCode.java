package org.infinity.passport.domain;

import java.io.Serializable;

import org.infinity.passport.dto.MongoOAuth2AuthorizationCodeDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.OAuth2Authentication;

@Document
public class MongoOAuth2AuthorizationCode implements Serializable {

    private static final long    serialVersionUID = 1L;

    @Id
    private String               id;

    private String               code;

    private OAuth2Authentication authentication;

    public MongoOAuth2AuthorizationCode() {
        super();
    }

    @PersistenceConstructor
    public MongoOAuth2AuthorizationCode(String code, OAuth2Authentication authentication) {
        this.code = code;
        this.authentication = authentication;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public OAuth2Authentication getAuthentication() {
        return authentication;
    }

    public void setAuthentication(OAuth2Authentication authentication) {
        this.authentication = authentication;
    }

    public MongoOAuth2AuthorizationCodeDTO asDTO() {
        MongoOAuth2AuthorizationCodeDTO dest = new MongoOAuth2AuthorizationCodeDTO();
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

        MongoOAuth2AuthorizationCode that = (MongoOAuth2AuthorizationCode) o;

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
