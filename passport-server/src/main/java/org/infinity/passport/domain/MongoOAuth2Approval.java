package org.infinity.passport.domain;

import org.infinity.passport.dto.MongoOAuth2ApprovalDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.annotation.PersistenceConstructor;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.security.oauth2.provider.approval.Approval;

import java.io.Serializable;

@Document
public class MongoOAuth2Approval extends Approval implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

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

    public MongoOAuth2ApprovalDTO asDTO() {
        MongoOAuth2ApprovalDTO dest = new MongoOAuth2ApprovalDTO();
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

        MongoOAuth2Approval that = (MongoOAuth2Approval) o;

        return id != null ? id.equals(that.id) : that.id == null;
    }

    @Override
    public int hashCode() {
        return id != null ? id.hashCode() : 0;
    }
}
