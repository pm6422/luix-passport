package cn.luixtech.passport.server.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.Instant;

@Entity
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
    @ManyToOne
    private   Notification notification;
    private   String       status;
    private   Boolean      active;
    @Schema(description = "created time")
    @Column(updatable = false)
    protected Instant      createdAt;
    @Schema(description = "last modified time")
    protected Instant      modifiedAt;
}
