package org.infinity.passport.domain;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.DictItemDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the DictItem entity.
 */
@Document(collection = "DictItem")
public class DictItem extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    private String            dictCode;

    private String            dictName;

    private String            dictItemCode;

    private String            dictItemName;

    private String            remark;

    private Boolean           enabled;

    public DictItem() {
        super();
    }

    /**
     * Constructor for creating operation
     * @param dictCode
     * @param dictName
     * @param dictItemCode
     * @param dictItemName
     * @param remark
     * @param enabled
     */
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
    public boolean equals(Object that) {
        if (this == that) {
            return true;
        }
        if (that == null) {
            return false;
        }
        if (getClass() != that.getClass()) {
            return false;
        }
        DictItem other = (DictItem) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDictCode() == null ? other.getDictCode() == null
                        : this.getDictCode().equals(other.getDictCode()))
                && (this.getDictName() == null ? other.getDictName() == null
                        : this.getDictName().equals(other.getDictName()))
                && (this.getDictItemCode() == null ? other.getDictItemCode() == null
                        : this.getDictItemCode().equals(other.getDictItemCode()))
                && (this.getDictItemName() == null ? other.getDictItemName() == null
                        : this.getDictItemName().equals(other.getDictItemName()))
                && (this.getRemark() == null ? other.getRemark() == null : this.getRemark().equals(other.getRemark()))
                && (this.getEnabled() == null ? other.getEnabled() == null
                        : this.getEnabled().equals(other.getEnabled()));
    }

    @Override
    public int hashCode() {
        final int prime = 31;
        int result = 1;
        result = prime * result + ((getId() == null) ? 0 : getId().hashCode());
        result = prime * result + ((getDictCode() == null) ? 0 : getDictCode().hashCode());
        result = prime * result + ((getDictName() == null) ? 0 : getDictName().hashCode());
        result = prime * result + ((getDictItemCode() == null) ? 0 : getDictItemCode().hashCode());
        result = prime * result + ((getDictItemName() == null) ? 0 : getDictItemName().hashCode());
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        return result;
    }

    public DictItemDTO asDTO() {
        DictItemDTO dto = new DictItemDTO();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    public static DictItem of(Dict dto) {
        DictItem target = new DictItem();
        BeanUtils.copyProperties(dto, target);
        return target;
    }
}