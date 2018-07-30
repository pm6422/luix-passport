package org.infinity.passport.dto;

import java.io.Serializable;

import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;

import org.infinity.passport.domain.base.AbstractAuditableDomain;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;

@ApiModel("数据字典DTO")
public class DictDTO extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 4885526690107887683L;

    @ApiModelProperty("数据字典ID")
    private String            id;

    @ApiModelProperty(value = "字典编号")
    private String            dictCode;

    @ApiModelProperty(value = "字典名称", required = true)
    @NotNull
    @Size(max = 50)
    private String            dictName;

    @ApiModelProperty(value = "备注")
    private String            remark;

    @ApiModelProperty(value = "是否可用")
    private Boolean           enabled;

    public DictDTO() {
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDictCode() {
        return dictCode;
    }

    public void setDictCode(String dictCode) {
        this.dictCode = dictCode;
    }

    public String getDictName() {
        return dictName;
    }

    public void setDictName(String dictName) {
        this.dictName = dictName;
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

    @Override
    public String toString() {
        return "DictDTO [dictId=" + id + ", dictCode=" + dictCode + ", dictName=" + dictName + ", remark=" + remark
                + ", enabled=" + enabled + "]";
    }
}
