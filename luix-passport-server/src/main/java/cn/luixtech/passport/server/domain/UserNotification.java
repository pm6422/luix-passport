package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractAuditableDomain;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.ManyToOne;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserNotification extends AbstractAuditableDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String STATUS_READ      = "READ";
    public static final  String STATUS_UNREAD    = "UNREAD";

    private String       userId;
    @ManyToOne(fetch = FetchType.LAZY)
    private Notification notification;
    private String       status;
    private Boolean      active;
}
