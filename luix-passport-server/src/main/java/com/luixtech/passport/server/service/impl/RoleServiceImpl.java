package com.luixtech.passport.server.service.impl;

import com.luixtech.passport.server.domain.Role;
import com.luixtech.passport.server.domain.RolePermission;
import com.luixtech.passport.server.dto.ManagedRole;
import com.luixtech.passport.server.repository.RolePermissionRepository;
import com.luixtech.passport.server.repository.RoleRepository;
import com.luixtech.passport.server.service.RolePermissionService;
import com.luixtech.passport.server.service.RoleService;
import com.luixtech.utilities.exception.DataNotFoundException;
import com.luixtech.utilities.exception.DuplicationException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Map;
import java.util.Set;

@Service
@AllArgsConstructor
@Slf4j
@SuppressWarnings("null")
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final RolePermissionRepository rolePermissionRepository;
    private final RolePermissionService rolePermissionService;

    @Override
    public Page<Role> find(Pageable pageable, String id) {
        // Ignore a query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Role criteria = new Role();
        criteria.setId(id);
        Example<Role> queryExample = Example.of(criteria, matcher);
        return roleRepository.findAll(queryExample, pageable);
    }

    @Override
    public Set<String> findAllIds() {
        return roleRepository.findAll().stream()
                .map(Role::getId)
                .collect(java.util.stream.Collectors.toSet());
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public Role insert(ManagedRole managedRole) {
        Role domain = managedRole.toRole();
        if (roleRepository.findById(domain.getId()).isPresent()) {
            throw new DuplicationException(Map.of("id", domain.getId()));
        }
        roleRepository.save(domain);
        log.info("Created role: {}", domain);

        if (CollectionUtils.isNotEmpty(managedRole.getPermissionIds())) {
            List<RolePermission> rolePermissions = managedRole.getPermissionIds().stream()
                    .map(permissionId -> {
                        RolePermission rolePermission = new RolePermission();
                        rolePermission.setRoleId(domain.getId());
                        rolePermission.setPermissionId(permissionId);
                        return rolePermission;
                    }).toList();
            rolePermissionRepository.saveAll(rolePermissions);
            log.info("Created role permissions: {}", rolePermissions);
        }
        return domain;
    }

    @Override
    public Role update(ManagedRole managedRole) {
        Role existingOne = roleRepository.findById(managedRole.getId())
                .orElseThrow(() -> new DataNotFoundException(managedRole.getId()));
        existingOne.setRemark(managedRole.getRemark());

        if (CollectionUtils.isNotEmpty(managedRole.getPermissionIds())) {
            rolePermissionService.update(existingOne.getId(), managedRole.getPermissionIds());
        }

        roleRepository.save(existingOne);
        log.info("Updated role: {}", existingOne);
        return existingOne;
    }
}
