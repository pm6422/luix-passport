package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
public class Notification extends AbstractUpdatableDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String TYPE_SYSTEM      = "SYSTEM";
    public static final  String TYPE_PERSONAL    = "PERSONAL";

    private String title;
    private String content;
    private String type;
    private String sender;
    private String senderEmail;
}
