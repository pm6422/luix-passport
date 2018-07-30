package org.infinity.passport.domain;

import java.io.Serializable;

import javax.validation.constraints.NotNull;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Spring Data MongoDB collection for the UserAuthority entity.
 */
@Document
public class UserAuthority extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    private String            userId;

    @NotNull
    private String            authorityName;

    /**
     * Constructor for creating operation
     * @param userId
     * @param authorityName
     */
    public UserAuthority(String userId, String authorityName) {
        super();
        this.userId = userId;
        this.authorityName = authorityName;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getAuthorityName() {
        return authorityName;
    }

    public void setAuthorityName(String authorityName) {
        this.authorityName = authorityName;
    }

    @Override
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        UserAuthority other = (UserAuthority) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getUserId() == null ? other.getUserId() == null : this.getUserId().equals(other.getUserId()))
                && (this.getAuthorityName() == null ? other.getAuthorityName() == null
                        : this.getAuthorityName().equals(other.getAuthorityName()))
                && (this.getCreatedTime() == null ? other.getCreatedTime() == null
                        : this.getCreatedTime().equals(other.getCreatedTime()))
                && (this.getCreatedBy() == null ? other.getCreatedBy() == null
                        : this.getCreatedBy().equals(other.getCreatedBy()))
                && (this.getModifiedTime() == null ? other.getModifiedTime() == null
                        : this.getModifiedTime().equals(other.getModifiedTime()))
                && (this.getModifiedBy() == null ? other.getModifiedBy() == null
                        : this.getModifiedBy().equals(other.getModifiedBy()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getUserId() == null) ? 0 : getUserId().hashCode());
        result = prime * result + ((getAuthorityName() == null) ? 0 : getAuthorityName().hashCode());
        result = prime * result + ((getCreatedTime() == null) ? 0 : getCreatedTime().hashCode());
        result = prime * result + ((getCreatedBy() == null) ? 0 : getCreatedBy().hashCode());
        result = prime * result + ((getModifiedTime() == null) ? 0 : getModifiedTime().hashCode());
        result = prime * result + ((getModifiedBy() == null) ? 0 : getModifiedBy().hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "UserAuthority [id=" + id + ", userId=" + userId + ", authorityName=" + authorityName
                + ", getCreatedBy()=" + getCreatedBy() + ", getCreatedTime()=" + getCreatedTime() + ", getModifiedBy()="
                + getModifiedBy() + ", getModifiedTime()=" + getModifiedTime() + "]";
    }
}
