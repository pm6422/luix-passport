package cn.luixtech.passport.server.domain.base.listener;

import cn.luixtech.passport.server.domain.base.*;
import cn.luixtech.passport.server.service.TableSeqNumberService;
import com.google.common.base.CaseFormat;
import com.luixtech.utilities.annotation.IncKey;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import lombok.NonNull;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.BeansException;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.util.ReflectionUtils;

import java.util.Objects;

public class AuditableEntityListener implements ApplicationContextAware {

    private ApplicationContext applicationContext;

    @Override
    public void setApplicationContext(@NonNull ApplicationContext applicationContext) throws BeansException {
        this.applicationContext = applicationContext;
    }

    @PrePersist
    public void prePersist(Object entity) {
        setIncKey(entity);

        if (entity instanceof AbstractUpdatableDomain) {
            ((AbstractUpdatableDomain) entity).prePersist();
            return;
        }
        if (entity instanceof AbstractUpdateDomain) {
            ((AbstractUpdateDomain) entity).prePersist();
            return;
        }
        if (entity instanceof AbstractCreatableDomain) {
            ((AbstractCreatableDomain) entity).prePersist();
            return;
        }
        if (entity instanceof AbstractCreationDomain) {
            ((AbstractCreationDomain) entity).prePersist();
            return;
        }
        if (entity instanceof AbstractBaseDomain) {
            ((AbstractBaseDomain) entity).prePersist();
        }
    }

    @PreUpdate
    public void preUpdate(Object entity) {
        if (entity instanceof AbstractUpdatableDomain) {
            ((AbstractUpdatableDomain) entity).preUpdate();
        }
        if (entity instanceof AbstractUpdateDomain) {
            ((AbstractUpdateDomain) entity).preUpdate();
        }
    }

    private void setIncKey(Object entity) {
        ReflectionUtils.doWithFields(entity.getClass(), field -> {
            if (field.isAnnotationPresent(IncKey.class)) {
                ReflectionUtils.makeAccessible(field);
                Object numValue = field.get(entity);
                if (numValue == null || numValue.toString().isEmpty()) {
                    // Creation operation if it is null
                    // Assign id with a user customized one
                    IncKey incKeyAnnotation = field.getAnnotation(IncKey.class);
                    String currentMaxNum;

                    // Convert pascal name to underscore one
                    String tableName = CaseFormat.UPPER_CAMEL.to(CaseFormat.LOWER_UNDERSCORE, entity.getClass().getSimpleName());

                    if (StringUtils.isEmpty(Objects.requireNonNull(incKeyAnnotation).prefix())) {
                        currentMaxNum = StringUtils.EMPTY + applicationContext.getBean(TableSeqNumberService.class).getNextSeqNumber(tableName);
                    } else {
                        currentMaxNum = incKeyAnnotation.prefix() + applicationContext.getBean(TableSeqNumberService.class).getNextSeqNumber(tableName);
                    }
                    field.set(entity, currentMaxNum);
                }
            }
        });
    }
}