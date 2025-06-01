package cn.luixtech.passport.server.domain.base;

import com.luixtech.uidgenerator.core.id.IdGenerator;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;

import java.io.Serial;
import java.io.Serializable;

@Data
@MappedSuperclass
public abstract class AbstractBaseDomain implements Serializable {
    @Serial
    private static final long serialVersionUID = -322694592498870599L;

    /**
     * ID cannot be a Long type, because the number which exceeds 16 digits will be display as 0 in the front end.
     */
    @Schema(description = "ID")
    @Id
    protected String id;

    public void prePersist() {
        if (StringUtils.isEmpty(id)) {
            id = IdGenerator.generateId();
        }
    }
}
