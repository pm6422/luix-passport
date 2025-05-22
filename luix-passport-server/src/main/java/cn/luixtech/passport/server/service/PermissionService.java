package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.Permission;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface PermissionService {
    Page<Permission> find(Pageable pageable, String resourceType, String action);
}
