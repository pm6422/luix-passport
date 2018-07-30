package org.infinity.passport.domain.base;

import java.io.Serializable;
import java.time.Instant;

import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.annotation.LastModifiedDate;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 *
 */
public abstract class AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * Set the proper value when inserting. Value comes from getCurrentAuditor() of SpringSecurityAuditorAware
     * Note: id不指定值保存时才被认为插入操作
     */
    @CreatedBy
    private String            createdBy;

    /**
     * Set the current time when inserting.
     * Note: id不指定值保存时才被认为插入操作
     */
    @CreatedDate
    private Instant           createdTime;

    /**
     * Set the proper value when updating. Value comes from getCurrentAuditor() of SpringSecurityAuditorAware
     */
    @LastModifiedBy
    private String            modifiedBy;

    /**
     * Set the current time when updating.
     */
    @LastModifiedDate
    private Instant           modifiedTime;

    public String getCreatedBy() {
        return createdBy;
    }

    public void setCreatedBy(String createdBy) {
        this.createdBy = createdBy;
    }

    public Instant getCreatedTime() {
        return createdTime;
    }

    public void setCreatedTime(Instant createdTime) {
        this.createdTime = createdTime;
    }

    public String getModifiedBy() {
        return modifiedBy;
    }

    public void setModifiedBy(String modifiedBy) {
        this.modifiedBy = modifiedBy;
    }

    public Instant getModifiedTime() {
        return modifiedTime;
    }

    public void setModifiedTime(Instant modifiedTime) {
        this.modifiedTime = modifiedTime;
    }
}
