package org.infinity.passport.domain;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the DictItem entity.
 */
@Schema(description = "数据字典项")
@Document(collection = "DictItem")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class DictItem extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Schema(description = "字典编号", required = true)
    @NotNull
    @Indexed
    private String dictCode;

    @Schema(description = "字典名称", required = true)
    private String dictName;

    @Schema(description = "字典项编号", required = true)
    @NotNull
    @Size(min = 2, max = 50)
    @Pattern(regexp = "^[a-z0-9-]+$", message = "{EP5901}")
    @Indexed
    private String dictItemCode;

    @Schema(description = "字典项名称", required = true)
    @NotNull
    @Size(min = 2, max = 50)
    private String dictItemName;

    @Schema(description = "备注")
    private String remark;

    @Schema(description = "是否可用")
    private Boolean enabled;

}