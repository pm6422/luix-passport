package org.infinity.passport.domain.base;

import lombok.Data;
import org.springframework.data.annotation.*;

import java.io.Serializable;
import java.time.Instant;

/**
 * Abstract auditable domain for log createdBy, createdTime, modifiedBy and modifiedTime automatically.
 */
@Data
public abstract class AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 主键不要定义为Long型，因为定义为Long型的字段如果超过16位的话在前端页面会显示为0
     */
    @Id
    protected String id;

    /**
     * Set the proper value when inserting. Value comes from getCurrentAuditor() of SpringSecurityAuditorAware
     * Note: id不指定值保存时才被认为插入操作
     */
    @CreatedBy
    protected String createdBy;

    /**
     * Set the current time when inserting.
     * Note: id不指定值保存时才被认为插入操作
     */
    @CreatedDate
    protected Instant createdTime;

    /**
     * Set the proper value when updating. Value comes from getCurrentAuditor() of SpringSecurityAuditorAware
     */
    @LastModifiedBy
    protected String modifiedBy;

    /**
     * Set the current time when updating.
     */
    @LastModifiedDate
    protected Instant modifiedTime;
}
