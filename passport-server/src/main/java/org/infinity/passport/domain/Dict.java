package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the Dict entity.
 */
@Schema(description = "数据字典")
@Document(collection = "Dict")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Dict extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典编号", required = true)
    @NotNull
    @Size(min = 2, max = 50)
    @Indexed(unique = true)
    private String dictCode;

    @Schema(description = "字典名称", required = true)
    @NotNull
    @Size(min = 2, max = 50)
    private String dictName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否可用")
    private Boolean enabled;

    public Dict(String dictName, Boolean enabled) {
        this.dictName = dictName;
        this.enabled = enabled;
    }

    public Dict(String dictCode, String dictName, String remark, Boolean enabled) {
        super();
        this.dictCode = dictCode;
        this.dictName = dictName;
        this.remark = remark;
        this.enabled = enabled;
    }
}