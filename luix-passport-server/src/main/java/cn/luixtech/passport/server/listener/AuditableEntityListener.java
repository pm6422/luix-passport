package cn.luixtech.passport.server.listener;

import cn.luixtech.passport.server.domain.base.AbstractAuditableDomain;
import cn.luixtech.passport.server.domain.base.AbstractBaseDomain;
import cn.luixtech.passport.server.domain.base.AbstractCreationDomain;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;

public class AuditableEntityListener {
    @PrePersist
    public void prePersist(Object entity) {
        if (entity instanceof AbstractAuditableDomain) {
            ((AbstractAuditableDomain) entity).prePersist();
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
        if (entity instanceof AbstractAuditableDomain) {
            ((AbstractAuditableDomain) entity).preUpdate();
        }
    }
}