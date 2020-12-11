package org.infinity.passport.dto;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

@ApiModel("数据字典项DTO")
@Data
@ToString(callSuper = true)
public class DictItemDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = -3707895016151729120L;

    @ApiModelProperty("数据字典项ID")
    private String id;

    @ApiModelProperty(value = "字典项编号")
    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String dictItemCode;

    @ApiModelProperty(value = "字典项名称", required = true)
    @NotNull
    @Size(max = 1000)
    private String dictItemName;

    @ApiModelProperty(value = "字典编号", required = true)
    @NotNull
    private String dictCode;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

}
