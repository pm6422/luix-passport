package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractBaseDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.validation.constraints.NotEmpty;
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
public class SupportedTimezone extends AbstractBaseDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "utcOffset:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String utcOffset;

    private Boolean preset;
}
