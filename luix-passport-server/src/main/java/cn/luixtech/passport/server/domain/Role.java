package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
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
public class Role extends AbstractUpdatableDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String remark;
}
