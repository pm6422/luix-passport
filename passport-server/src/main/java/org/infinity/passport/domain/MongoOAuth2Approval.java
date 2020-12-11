package org.infinity.passport.domain;

import lombok.Data;
import org.infinity.passport.dto.MongoOAuth2ApprovalDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.approval.Approval;

import java.io.Serializable;

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

    public MongoOAuth2ApprovalDTO toDTO() {
        MongoOAuth2ApprovalDTO dest = new MongoOAuth2ApprovalDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }

}
