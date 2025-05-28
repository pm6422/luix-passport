package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.RolePermission;
import cn.luixtech.passport.server.repository.RolePermissionRepository;
import cn.luixtech.passport.server.service.RolePermissionService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.persistence.Tables.ROLE_PERMISSION;
import static cn.luixtech.passport.server.persistence.Tables.USER_ROLE;

@Service
@AllArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final DSLContext               dslContext;
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public Set<String> findPermissionIds(Set<String> roleIds) {
        return dslContext.selectDistinct(ROLE_PERMISSION.PERMISSION_ID)
                .from(ROLE_PERMISSION)
                .join(USER_ROLE).on(ROLE_PERMISSION.ROLE_ID.eq(USER_ROLE.ROLE_ID))
                .where(USER_ROLE.ROLE_ID.in(roleIds))
                .fetchSet(ROLE_PERMISSION.PERMISSION_ID);
    }

    @Override
    public void update(String roleId, Set<String> newPermissionIds) {
        if (CollectionUtils.isEmpty(newPermissionIds)) {
            return;
        }

        // 1. query existing permissions
        List<RolePermission> existingPermissions = rolePermissionRepository.findByRoleId(roleId);
        Set<String> existingPermissionIds = existingPermissions.stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());

        // 2. calculate difference
        Set<String> permissionsToDelete = existingPermissionIds.stream()
                .filter(permissionId -> !newPermissionIds.contains(permissionId))
                .collect(Collectors.toSet());

        Set<String> permissionsToAdd = newPermissionIds.stream()
                .filter(permissionId -> !existingPermissionIds.contains(permissionId))
                .collect(Collectors.toSet());

        // 3. execute update
        if (CollectionUtils.isNotEmpty(permissionsToDelete)) {
            rolePermissionRepository.deleteByRoleIdAndPermissionIdIn(roleId, permissionsToDelete);
        }
        if (CollectionUtils.isNotEmpty(permissionsToAdd)) {
            List<RolePermission> newRolePermissions = assignPermissionsToRole(roleId, permissionsToAdd);
            rolePermissionRepository.saveAll(newRolePermissions);
        }
    }

    private List<RolePermission> assignPermissionsToRole(String roleId, Set<String> permissionIds) {
        if (CollectionUtils.isEmpty(permissionIds)) {
            return Collections.emptyList();
        }
        return permissionIds.stream()
                .map(permissionId -> buildRolePermission(roleId, permissionId))
                .collect(Collectors.toList());
    }

    private RolePermission buildRolePermission(String roleId, String permissionId) {
        RolePermission rolePermission = new RolePermission();
        rolePermission.setRoleId(roleId);
        rolePermission.setPermissionId(permissionId);
        return rolePermission;
    }
}
