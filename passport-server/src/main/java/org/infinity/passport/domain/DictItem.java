package org.infinity.passport.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.DictItemDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the DictItem entity.
 */
@Document(collection = "DictItem")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class DictItem extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dictCode;

    private String dictName;

    private String dictItemCode;

    private String dictItemName;

    private String remark;

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

    public DictItemDTO toDTO() {
        DictItemDTO dto = new DictItemDTO();
        BeanUtils.copyProperties(this, dto);
        return dto;
    }

    public static DictItem of(DictItemDTO dto) {
        DictItem target = new DictItem();
        BeanUtils.copyProperties(dto, target);
        return target;
    }
}