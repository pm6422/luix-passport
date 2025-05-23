package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractCreationDomain;
import cn.luixtech.passport.server.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

@Entity
@EntityListeners(AuditableEntityListener.class)
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserAuthEvent extends AbstractCreationDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String AUTH_SUCCESS     = "AuthenticationSuccess";
    public static final  String AUTH_FAILURE     = "AuthenticationFailure";

    private String userId;
    private String event;
    private String remark;
}
