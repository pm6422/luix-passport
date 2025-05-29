package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
public class UserNotification extends AbstractUpdatableDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String STATUS_READ      = "READ";
    public static final  String STATUS_UNREAD    = "UNREAD";

    private   String       receiverId;
    /**
     * 一条Notification可以发送给多个用户 (体现在多个UserNotification记录中)
     * 一个用户通知 (UserNotification) 只能关联一条具体的通知
     */
    @ManyToOne
    private   Notification notification;
    private   String       status;
    private   Boolean      active;
}
