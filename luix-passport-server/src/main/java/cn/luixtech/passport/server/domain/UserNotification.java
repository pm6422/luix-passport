package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.listener.AuditableEntityListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserNotification implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String STATUS_READ      = "READ";
    public static final  String STATUS_UNREAD    = "UNREAD";

    @Schema(description = "ID")
    @Id
    protected String       id;
    private   String       receiverId;
    /**
     * 一条Notification可以发送给多个用户 (体现在多个UserNotification记录中)
     * 一个用户通知 (UserNotification) 只能关联一条具体的通知
     */
    @ManyToOne
    private   Notification notification;
    private   String       status;
    private   Boolean      active;
    @Schema(description = "created time")
    @Column(updatable = false)
    private   Instant      createdAt;
    @Schema(description = "last modified time")
    private   Instant      modifiedAt;
}
