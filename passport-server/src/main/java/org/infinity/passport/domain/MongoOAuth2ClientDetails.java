package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.ClientDetails;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;

@Schema(description = "单点登录客户端信息")
@Document(collection = "MongoOAuth2ClientDetails")
@Data
public class MongoOAuth2ClientDetails extends BaseClientDetails implements ClientDetails {

    private static final long serialVersionUID = 1L;

    public static final String INTERNAL_CLIENT_ID = "internal-client";

    public static final String INTERNAL_RAW_CLIENT_SECRET = "65G-HD9-4PD-j9F-HP5";

    @Id
    @org.codehaus.jackson.annotate.JsonProperty("client_id")
    @com.fasterxml.jackson.annotation.JsonProperty("client_id")
    private String clientId;

    @org.codehaus.jackson.annotate.JsonProperty("raw_client_secret")
    @com.fasterxml.jackson.annotation.JsonProperty("raw_client_secret")
    private String rawClientSecret;

    @PersistenceConstructor
    public MongoOAuth2ClientDetails() {
        super();
    }
}
