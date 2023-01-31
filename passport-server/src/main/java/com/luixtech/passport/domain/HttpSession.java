package com.luixtech.passport.domain;

import com.luixtech.passport.domain.base.AbstractAuditableDomain;
import lombok.Data;

import javax.persistence.Entity;
import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

/**
 * Http Session which is created by {@link org.springframework.session.data.mongo.MongoIndexedSessionRepository}
 * <p>
 * Please refer below classes for more details
 * {@link org.springframework.session.data.mongo.JdkMongoSessionConverter}
 * {@link org.springframework.session.data.mongo.MongoSession}
 */
@Entity
@Data
public class HttpSession extends AbstractAuditableDomain implements Serializable {
    private static final long     serialVersionUID = 4707283652283161571L;
    private              String   principal;
    private              Instant  created;
    private              Instant  accessed;
    private              Duration interval;
    private              Instant  expireAt;
}
