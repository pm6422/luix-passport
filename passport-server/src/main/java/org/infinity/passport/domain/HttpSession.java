package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
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
@Schema(description = "Http会话")
@Document
@Data
public class HttpSession implements Serializable {

    private static final long     serialVersionUID = -1L;
    @Id
    protected            String   id;
    @Schema(description = "用户名")
    private              String   principal;
    @Schema(description = "创建时间")
    private              Instant  created;
    @Schema(description = "访问时间")
    private              Instant  accessed;
    @Schema(description = "有效期")
    private              Duration interval;
    @Schema(description = "失效时间")
    private              Instant  expireAt;

}
