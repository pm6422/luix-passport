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
    private              String  jobType;
    private              String  payload;
    private              String  status;
    private              Instant createdAt;
    private              Instant processedAt;

    public void markAsProcessing() {
        this.status = STATUS_PROCESSING;
        this.processedAt = Instant.now();
    }

    public void markAsCompleted() {
        this.status = STATUS_COMPLETED;
    }

    public void markAsFailed() {
        this.status = STATUS_FAILED;
    }
}