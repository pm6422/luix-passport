package org.infinity.passport.domain;

import lombok.Data;
import lombok.EqualsAndHashCode;
import org.infinity.passport.domain.base.AbstractAuditableDomain;
import org.infinity.passport.dto.DictDTO;
import org.springframework.beans.BeanUtils;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.io.Serializable;

/**
 * Spring Data MongoDB collection for the Dict entity.
 */
@Document(collection = "Dict")
@Data
@EqualsAndHashCode(callSuper = true)
public class Dict extends AbstractAuditableDomain implements Serializable {

    private static final long serialVersionUID = 1L;

    @Id
    private String id;

    private String dictCode;

    private String dictName;

    private String remark;

    private Boolean enabled;

    public Dict() {
        super();
    }

    public Dict(String dictCode, String dictName, String remark, Boolean enabled) {
        super();
        this.dictCode = dictCode;
        this.dictName = dictName;
        this.remark = remark;
        this.enabled = enabled;
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