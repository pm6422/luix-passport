package org.infinity.passport.domain.useless;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.approval.Approval;

import java.io.Serializable;

@Schema(description = "单点登录授权信息")
@Document(collection = "MongoOAuth2Approval")
@Data
public class MongoOAuth2Approval extends Approval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @PersistenceConstructor
    public MongoOAuth2Approval() {
        super();
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

}
