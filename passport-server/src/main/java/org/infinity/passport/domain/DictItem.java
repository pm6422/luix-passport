package org.infinity.passport.domain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.springframework.data.mongodb.core.mapping.Document;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;
import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the DictItem entity.
 */
@ApiModel("数据字典项")
@Document(collection = "DictItem")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class DictItem extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @ApiModelProperty(value = "字典编号", required = true)
    @NotNull
    private String dictCode;

    @ApiModelProperty(value = "字典名称")
    private String dictName;

    @ApiModelProperty(value = "字典项编号")
    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String dictItemCode;

    @ApiModelProperty(value = "字典项名称", required = true)
    @NotNull
    @Size(max = 1000)
    private String dictItemName;

    @ApiModelProperty(value = "备注")
    private String remark;

    @ApiModelProperty(value = "是否可用")
    private Boolean enabled;

    public DictItem(String dictCode, String dictName, String dictItemCode, String dictItemName, String remark,
                    Boolean enabled) {
        super();
        this.dictCode = dictCode;
        this.dictName = dictName;
        this.dictItemCode = dictItemCode;
        this.dictItemName = dictItemName;
        this.remark = remark;
        this.enabled = enabled;
    }
}