package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.UserRole;
import cn.luixtech.passport.server.repository.UserRoleRepository;
import cn.luixtech.passport.server.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ANONYMOUS;
import static cn.luixtech.passport.server.domain.UserRole.ROLE_USER;
import static cn.luixtech.passport.server.persistence.Tables.USER_ROLE;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;
    private final DSLContext         dslContext;

    @Override
    public Set<String> findRoleIds(String username) {
        return dslContext.select(USER_ROLE.ROLE_ID)
                .from(USER_ROLE)
                .where(USER_ROLE.USERNAME.eq(username))
                .fetchSet(USER_ROLE.ROLE_ID);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void update(String username, Set<String> newRoleIds) {
        if (CollectionUtils.isEmpty(newRoleIds)) {
            return;
        }

        // 1. query existing roles
        List<UserRole> existingRoles = userRoleRepository.findByUsername(username);
        Set<String> existingRoleIds = existingRoles.stream()
                .map(UserRole::getRoleId)
                .collect(Collectors.toSet());

        // 2. calculate difference
        Set<String> rolesToDelete = existingRoleIds.stream()
                .filter(roleId -> !newRoleIds.contains(roleId))
                .collect(Collectors.toSet());

        Set<String> rolesToAdd = newRoleIds.stream()
                .filter(roleId -> !existingRoleIds.contains(roleId))
                .collect(Collectors.toSet());

        // 3. execute update
        if (CollectionUtils.isNotEmpty(rolesToDelete)) {
            userRoleRepository.deleteByUsernameAndRoleIdIn(username, rolesToDelete);
        }
        if (CollectionUtils.isNotEmpty(rolesToAdd)) {
            List<UserRole> newUserRoles = assignRolesToUser(username, rolesToAdd);
            userRoleRepository.saveAll(newUserRoles);
        }
    }

    @Override
    public List<UserRole> assignWithDefaults(String username, Set<String> roleIds) {
        List<UserRole> userRoles = assignRolesToUser(username, roleIds);

        // set default user roles
        UserRole anoRole = buildUserRole(username, ROLE_ANONYMOUS);
        UserRole userRole = buildUserRole(username, ROLE_USER);

        if (!userRoles.contains(anoRole)) {
            userRoles.add(anoRole);
        }
        if (!userRoles.contains(userRole)) {
            userRoles.add(userRole);
        }
        return userRoles;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUsername(String username) {
        userRoleRepository.deleteByUsername(username);
    }

    private List<UserRole> assignRolesToUser(String username, Set<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return new ArrayList<>();
        }
        return roleIds.stream()
                .map(auth -> buildUserRole(username, auth))
                .collect(Collectors.toList());
    }

    private UserRole buildUserRole(String username, String authority) {
        UserRole userRole = new UserRole();
        userRole.setUsername(username);
        userRole.setRoleId(authority);
        return userRole;
    }
}
