package org.infinity.passport.domain;

import org.infinity.passport.dto.MongoOAuth2ClientDetailsDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

@Document(collection = "MongoOAuth2ClientDetails")
public class MongoOAuth2ClientDetails extends BaseClientDetails implements ClientDetails {

    private static final long  serialVersionUID           = 1L;

    public static final String INTERNAL_CLIENT_ID = "internal-client";

    public static final String INTERNAL_RAW_CLIENT_SECRET = "65G-HD9-4PD-j9F-HP5";

    @Id
    @org.codehaus.jackson.annotate.JsonProperty("client_id")
    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String             clientId;

    @org.codehaus.jackson.annotate.JsonProperty("raw_client_secret")
    @com.fasterxml.jackson.annotation.JsonProperty("raw_client_secret")
    private String             rawClientSecret;

    @PersistenceConstructor
    public MongoOAuth2ClientDetails() {
        super();
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getRawClientSecret() {
        return rawClientSecret;
    }

    public void setRawClientSecret(String rawClientSecret) {
        this.rawClientSecret = rawClientSecret;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }

        MongoOAuth2ClientDetails that = (MongoOAuth2ClientDetails) o;
        return clientId != null ? clientId.equals(that.clientId) : that.clientId == null;
    }

    @Override
    public int hashCode() {
        return clientId != null ? clientId.hashCode() : 0;
    }

    public MongoOAuth2ClientDetailsDTO asDTO() {
        MongoOAuth2ClientDetailsDTO dest = new MongoOAuth2ClientDetailsDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }

    public static MongoOAuth2ClientDetails of(MongoOAuth2ClientDetailsDTO dto) {
        MongoOAuth2ClientDetails dest = new MongoOAuth2ClientDetails();
        BeanUtils.copyProperties(dto, dest);
        return dest;
    }
}
