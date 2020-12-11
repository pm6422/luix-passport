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
     * ID data type must NOT be Long, because the number which exceeds 16 digits will be display as 0 at front end.
     * e.g. the value 526373442322434543 will be displayed as 526373442322434500 in front end
     * If id is null, save operation equals insert, or else save operation equals update
     */
    @Id
    protected String id;

    /**
     * Set the proper value when inserting. Value comes from SpringSecurityAuditorAware.getCurrentAuditor()
     */
    @CreatedBy
    protected String createdBy;

    /**
     * Set the current time when inserting.
     */
    @CreatedDate
    protected Instant createdTime;

    /**
     * Set the proper value when updating. Value comes from SpringSecurityAuditorAware.getCurrentAuditor()
     */
    @LastModifiedBy
    protected String modifiedBy;

    /**
     * Set the current time when updating.
     */
    @LastModifiedDate
    protected Instant modifiedTime;
}
