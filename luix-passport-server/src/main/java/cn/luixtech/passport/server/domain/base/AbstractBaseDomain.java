package cn.luixtech.passport.server.domain.base;

import cn.luixtech.passport.server.service.TableSeqNumberService;
import com.google.common.base.CaseFormat;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import com.luixtech.utilities.annotation.IncKey;
import io.swagger.v3.oas.annotations.media.Schema;
import jakarta.persistence.Id;
import jakarta.persistence.MappedSuperclass;
import lombok.Data;
import org.apache.commons.lang3.StringUtils;
import org.springframework.util.ReflectionUtils;

import java.io.Serial;
import java.io.Serializable;
import java.util.Objects;

import static cn.luixtech.passport.server.PassportServerApplication.applicationContext;

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

        ReflectionUtils.doWithFields(this.getClass(), field -> {
            if (field.isAnnotationPresent(IncKey.class)) {
                ReflectionUtils.makeAccessible(field);
                Object numValue = field.get(this);
                if (numValue == null || numValue.toString().isEmpty()) {
                    // Creation operation if it is null
                    // Assign id with a user customized one
                    IncKey incKeyAnnotation = field.getAnnotation(IncKey.class);
                    String num;

                    // Convert pascal name to underscore one
                    String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, this.getClass().getSimpleName());

                    if (StringUtils.isEmpty(Objects.requireNonNull(incKeyAnnotation).prefix())) {
                        num = StringUtils.EMPTY + applicationContext.getBean(TableSeqNumberService.class).getNextSeqNumber(tableName);
                    } else {
                        num = incKeyAnnotation.prefix() + applicationContext.getBean(TableSeqNumberService.class).getNextSeqNumber(tableName);
                    }
                    field.set(this, num);
                }
            }
        });
    }
}
