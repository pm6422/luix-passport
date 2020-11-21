package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.Field;

import javax.validation.constraints.NotNull;
import java.io.Serializable;
import java.time.Instant;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * Persist AuditEvent managed by the Spring Boot actuator @see org.springframework.boot.actuate.audit.AuditEvent
 */
@Document(collection = "PersistentAuditEvent")
@Data
@EqualsAndHashCode
public class PersistentAuditEvent implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    @NotNull
    private String principal;

    @Field("event_date")
    private Instant auditEventDate;

    @Field("event_type")
    private String auditEventType;

    @Indexed(expireAfterSeconds = 0)//Expire Documents at a Specific Clock Time
    private Date expiration;

    private Map<String, String> data = new HashMap<>();

}
