package org.infinity.passport.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Pattern;
import javax.validation.constraints.Size;

import org.infinity.passport.domain.base.AbstractAuditableDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("数据字典项DTO")
public class DictItemDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = -3707895016151729120L;

    @ApiModelProperty("数据字典项ID")
    private String            id;

    @ApiModelProperty(value = "字典项编号")
    @NotNull
    @Size(min = 1, max = 64)
    @Pattern(regexp = "^[a-zA-Z0-9]*$")
    private String            dictItemCode;

    @ApiModelProperty(value = "字典项名称", required = true)
    @NotNull
    @Size(max = 1000)
    private String            dictItemName;

    @ApiModelProperty(value = "字典编号", required = true)
    @NotNull
    private String            dictCode;

    @ApiModelProperty(value = "字典名称")
    private String            dictName;

    @ApiModelProperty(value = "备注")
    private String            remark;

    @ApiModelProperty(value = "是否可用")
    private Boolean           enabled;

    public DictItemDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDictItemCode() {
        return dictItemCode;
    }

    public void setDictItemCode(String dictItemCode) {
        this.dictItemCode = dictItemCode;
    }

    public String getDictItemName() {
        return dictItemName;
    }

    public void setDictItemName(String dictItemName) {
        this.dictItemName = dictItemName;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }

    public Boolean getEnabled() {
        return enabled;
    }

    public void setEnabled(Boolean enabled) {
        this.enabled = enabled;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
    }

    @Override
    public String toString() {
        return "DictItemDTO [dictItemId=" + id + ", dictItemCode=" + dictItemCode + ", dictItemName=" + dictItemName
                + ", dictCode=" + dictCode + ", remark=" + remark + ", dictName=" + dictName + ", enabled=" + enabled
                + "]";
    }
}
