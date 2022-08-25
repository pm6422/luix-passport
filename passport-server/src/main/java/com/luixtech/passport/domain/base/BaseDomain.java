package com.luixtech.passport.domain.base;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Data;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class BaseDomain implements Serializable {
    private static final long serialVersionUID = -322694592498870599L;

    /**
     * ID should NOT be Long type, because the number which exceeds 16 digits will be display as 0 in front end.
     * e.g. the value 526373442322434543 will be displayed as 526373442322434500 in front end
     * If id is null, save operation equals insert, or else save operation equals update
     */
    @Schema(description = "ID")
    @Id
    @GenericGenerator(name = "uuid-hex", strategy = "uuid.hex")
    @GeneratedValue(generator = "uuid-hex")
    protected String id;

}
