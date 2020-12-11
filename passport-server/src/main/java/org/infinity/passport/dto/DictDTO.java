package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel("数据字典DTO")
@Data
public class DictDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 4885526690107887683L;

    @ApiModelProperty("数据字典ID")
    private String id;

    @ApiModelProperty(value = "字典编号")
    private String dictCode;

    @ApiModelProperty(value = "字典名称", required = true)
    @NotNull
    @Size(max = 50)
    private String dictName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

}
