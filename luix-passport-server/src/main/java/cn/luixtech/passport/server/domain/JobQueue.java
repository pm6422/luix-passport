package cn.luixtech.passport.server.domain;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.UuidGenerator;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class JobQueue {
    public static final String STATUS_PENDING    = "pending";
    public static final String STATUS_PROCESSING = "processing";
    public static final String STATUS_COMPLETED  = "completed";
    public static final String STATUS_FAILED     = "failed";

    @Id
    @UuidGenerator
    @Column(length = 36)
    private String  id;
    private String  channel;
    private String  payload;
    private Integer priority;
    private String  status;
    private String  error;
    private Instant processedAt;
    private Instant createdAt;

    public JobQueue(String channel, String payload) {
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