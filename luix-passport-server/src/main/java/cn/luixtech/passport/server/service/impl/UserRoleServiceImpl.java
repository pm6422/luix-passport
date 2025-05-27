package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.UserRole;
import cn.luixtech.passport.server.repository.UserRoleRepository;
import cn.luixtech.passport.server.service.UserRoleService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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
    public Set<String> findRoleIds(String userId) {
        return dslContext.select(USER_ROLE.ROLE_ID)
                .from(USER_ROLE)
                .where(USER_ROLE.USER_ID.eq(userId))
                .fetchSet(USER_ROLE.ROLE_ID);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(String userId, Set<String> newRoleIds) {
        if (CollectionUtils.isEmpty(newRoleIds)) {
            return;
        }

        // 1. query existing roles
        List<UserRole> existingRoles = userRoleRepository.findByUserId(userId);
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
            userRoleRepository.deleteByUserIdAndRoleIdIn(userId, rolesToDelete);
        }
        if (CollectionUtils.isNotEmpty(rolesToAdd)) {
            List<UserRole> newUserRoles = assignRolesToUser(userId, rolesToAdd);
            userRoleRepository.saveAll(newUserRoles);
        }
    }

    @Override
    public List<UserRole> assignWithDefaults(String userId, Set<String> roleIds) {
        List<UserRole> userRoles = assignRolesToUser(userId, roleIds);

        // set default user roles
        UserRole anoRole = buildUserRole(userId, ROLE_ANONYMOUS);
        UserRole userRole = buildUserRole(userId, ROLE_USER);

        if (!userRoles.contains(anoRole)) {
            userRoles.add(anoRole);
        }
        if (!userRoles.contains(userRole)) {
            userRoles.add(userRole);
        }
        return userRoles;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByUserId(String id) {
        userRoleRepository.deleteByUserId(id);
    }

    private List<UserRole> assignRolesToUser(String userId, Set<String> roleIds) {
        if (CollectionUtils.isEmpty(roleIds)) {
            return Collections.emptyList();
        }
        return roleIds.stream()
                .map(auth -> buildUserRole(userId, auth))
                .collect(Collectors.toList());
    }

    private UserRole buildUserRole(String userId, String authority) {
        UserRole userRole = new UserRole();
        userRole.setUserId(userId);
        userRole.setRoleId(authority);
        return userRole;
    }
}
