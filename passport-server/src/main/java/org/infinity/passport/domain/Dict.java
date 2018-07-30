package org.infinity.passport.domain;

import java.io.Serializable;

import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.DictDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

/**
 * Spring Data MongoDB collection for the Dict entity.
 */
@Document
public class Dict extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String            id;

    private String            dictCode;

    private String            dictName;

    private String            remark;

    private Boolean           enabled;

    public Dict() {
        super();
    }

    /**
     * Constructor for creating operation
     * @param dictCode
     * @param dictName
     * @param remark
     * @param enabled
     */
    public Dict(String dictCode, String dictName, String remark, Boolean enabled) {
        super();
        this.dictCode = dictCode;
        this.dictName = dictName;
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
        Dict other = (Dict) that;
        return (this.getId() == null ? other.getId() == null : this.getId().equals(other.getId()))
                && (this.getDictCode() == null ? other.getDictCode() == null
                        : this.getDictCode().equals(other.getDictCode()))
                && (this.getDictName() == null ? other.getDictName() == null
                        : this.getDictName().equals(other.getDictName()))
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
        result = prime * result + ((getRemark() == null) ? 0 : getRemark().hashCode());
        result = prime * result + ((getEnabled() == null) ? 0 : getEnabled().hashCode());
        return result;
    }

    public DictDTO asDTO() {
        DictDTO dest = new DictDTO();
        BeanUtils.copyProperties(this, dest);
        return dest;
    }

    public static Dict of(DictDTO dto) {
        Dict dest = new Dict();
        BeanUtils.copyProperties(dto, dest);
        return dest;
    }
}