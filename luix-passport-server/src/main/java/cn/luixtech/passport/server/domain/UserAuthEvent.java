package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractCreatableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class UserAuthEvent extends AbstractCreatableDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID = 1L;
    public static final  String AUTH_SUCCESS     = "AuthenticationSuccess";
    public static final  String AUTH_FAILURE     = "AuthenticationFailure";

    private String userId;
    private String event;
    private String remark;
}
