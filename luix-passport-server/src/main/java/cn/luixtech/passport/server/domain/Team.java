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
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Team extends AbstractUpdatableDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    private String  remark;
    private Boolean enabled;

    @Basic(fetch = FetchType.LAZY)
    @Column(columnDefinition = "bytea", nullable = false)
    private byte[] photo;
}
