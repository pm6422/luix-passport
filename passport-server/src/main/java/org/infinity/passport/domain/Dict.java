package org.infinity.passport.domain;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.DictDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the Dict entity.
 */
@Document(collection = "Dict")
@Data
@ToString(callSuper = true)
@NoArgsConstructor
public class Dict extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    private String dictCode;

    private String dictName;

    private String remark;

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

    public DictDTO toDTO() {
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