package com.luixtech.passport.server.domain;

import com.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.idgenerator.TsidGenerator;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;

@Data
@Entity
@NoArgsConstructor
@AllArgsConstructor
public class Inbox {

    @Id
    @GeneratedValue
    @TsidGenerator
    @Column(length = 18)
    private             String id;
    public static final String STATUS_READ   = "READ";
    public static final String STATUS_UNREAD = "UNREAD";

    private String       receiverId;
    /**
     * 一条Notification可以发送给多个用户 (体现在多个Inbox记录中)
     * 一个用户通知 (Inbox) 只能关联一条具体的通知
     */
    @ManyToOne
    private Notification notification;
    private String       status;

    @Column(nullable = false, updatable = false)
    private Instant createdAt;

    @Column(nullable = false)
    private Instant updatedAt;

    @Column(updatable = false)
    private String createdBy;

    private String updatedBy;

    @PrePersist
    protected void onCreate() {
        createdAt = Instant.now();
        updatedAt = Instant.now();
        createdBy = AuthUtils.getCurrentUsername();
        updatedBy = createdBy;
    }

    @PreUpdate
    protected void onUpdate() {
        updatedAt = Instant.now();
        updatedBy = AuthUtils.getCurrentUsername();
    }
}
