package org.infinity.passport.domain;

import io.swagger.annotations.ApiModelProperty;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;
import java.time.Duration;
import java.time.Instant;

/**
 * Http会话
 */
@Document(collection = "HttpSession")
public class HttpSession implements Serializable {

    private static final long     serialVersionUID = -1L;
    @Id
    protected            String   id;
    @ApiModelProperty("用户名")
    private              String   principal;
    @ApiModelProperty("创建时间")
    private              Instant  created;
    @ApiModelProperty("访问时间")
    private              Instant  accessed;
    @ApiModelProperty("有效期")
    private              Duration interval;
    @ApiModelProperty("失效时间")
    private              Instant  expireAt;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public Instant getCreated() {
        return created;
    }

    public void setCreated(Instant created) {
        this.created = created;
    }

    public Instant getAccessed() {
        return accessed;
    }

    public void setAccessed(Instant accessed) {
        this.accessed = accessed;
    }

    public Duration getInterval() {
        return interval;
    }

    public void setInterval(Duration interval) {
        this.interval = interval;
    }

    public Instant getExpireAt() {
        return expireAt;
    }

    public void setExpireAt(Instant expireAt) {
        this.expireAt = expireAt;
    }

    @Override
    public String toString() {
        return "HttpSession{" +
                "id='" + id + '\'' +
                ", principal='" + principal + '\'' +
                ", created=" + created +
                ", accessed=" + accessed +
                ", interval=" + interval +
                ", expireAt=" + expireAt +
                '}';
    }
}
