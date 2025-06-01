package cn.luixtech.passport.server.domain;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
import cn.luixtech.passport.server.domain.base.listener.AuditableEntityListener;
import com.luixtech.utilities.annotation.IncKey;
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
public class DataDict extends AbstractUpdatableDomain implements Serializable {
    @Serial
    private static final long   serialVersionUID       = 1L;
    public static final  String CATEGORY_CODE_TIMEZONE = "Timezone";

    @IncKey(prefix = "DCT")
    @Column(unique = true, nullable = false)
    private String num;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "categoryCode:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String categoryCode;

    @Schema(requiredMode = Schema.RequiredMode.REQUIRED)
    @NotEmpty(message = "dictCode:{Validation.NotEmpty}")
    @Column(nullable = false)
    private String dictCode;

    private String dictName;

    private String remark;

    private Boolean enabled;
}
