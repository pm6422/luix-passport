package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractBaseDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class JobQueue extends AbstractBaseDomain implements Serializable {
    @Serial
    private static final long    serialVersionUID  = 1L;
    public static final  String  STATUS_PENDING    = "pending";
    public static final  String  STATUS_PROCESSING = "processing";
    public static final  String  STATUS_COMPLETED  = "completed";
    public static final  String  STATUS_FAILED     = "failed";
    private              Boolean broadcastFlag;
    private              String  channel;
    private              String  payload;
    private              Integer priority;
    private              String  status;
    private              String  error;
    private              Instant createdAt;
    private              Instant processedAt;

    public JobQueue(String channel, String payload) {
        this.channel = channel;
        this.payload = payload;
        this.priority = 0;
        this.status = STATUS_PENDING;
        this.createdAt = Instant.now();
    }

    public JobQueue(String channel, String payload, Boolean broadcastFlag) {
        this.broadcastFlag = broadcastFlag;
        this.channel = channel;
        this.payload = payload;
        this.priority = 0;
        this.status = STATUS_PENDING;
        this.createdAt = Instant.now();
    }

    public void markAsProcessing() {
        this.status = STATUS_PROCESSING;
        this.processedAt = Instant.now();
    }

    public void markAsCompleted() {
        this.status = STATUS_COMPLETED;
        this.processedAt = Instant.now();
    }

    public void markAsFailed(String error) {
        this.status = STATUS_FAILED;
        this.processedAt = Instant.now();
    }
}