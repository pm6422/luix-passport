package com.luixtech.passport.server.service.impl;

import com.luixtech.passport.server.domain.RolePermission;
import com.luixtech.passport.server.repository.RolePermissionRepository;
import com.luixtech.passport.server.service.RolePermissionService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;

import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final RolePermissionRepository rolePermissionRepository;

    @Override
    public Set<String> findPermissionIds(Set<String> roleIds) {
        return rolePermissionRepository.findByRoleIdIn(roleIds).stream()
                .map(RolePermission::getPermissionId)
                .collect(Collectors.toSet());
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
