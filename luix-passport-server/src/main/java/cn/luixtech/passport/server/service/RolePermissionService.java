package cn.luixtech.passport.server.service;

import java.util.Set;

public interface RolePermissionService {

    Set<String> findPermissionIds(Set<String> roleIds);

    void update(String roleId, Set<String> newPermissionIds);
}
