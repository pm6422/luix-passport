package org.infinity.passport.domain;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

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
@Document
@Data
public class HttpSession implements Serializable {

    private static final long     serialVersionUID = -1L;
    @Id
    protected            String   id;
    private              String   principal;
    private              Instant  created;
    private              Instant  accessed;
    private              Duration interval;
    private              Instant  expireAt;
}
