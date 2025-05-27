package cn.luixtech.passport.server.domain.base.listener;

import cn.luixtech.passport.server.domain.base.AbstractUpdatableDomain;
import cn.luixtech.passport.server.domain.base.AbstractBaseDomain;
import cn.luixtech.passport.server.domain.base.AbstractCreatableDomain;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AuditableEntityListener {
    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof AbstractUpdatableDomain) {
            ((AbstractUpdatableDomain) entity).prePersist();
            return;
        }
        if (entity instanceof AbstractCreatableDomain) {
            ((AbstractCreatableDomain) entity).prePersist();
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
    }
}