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

import java.util.ArrayList;
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
    public List<UserRole> generate(String userId, Set<String> newAuthorities) {
        List<UserRole> userRoles = new ArrayList<>();
        if (newAuthorities != null) {
            userRoles = newAuthorities.stream()
                    .map(auth -> build(userId, auth))
                    .collect(Collectors.toList());
        }

        // set default user newAuthorities
        UserRole anoAuth = build(userId, ROLE_ANONYMOUS);
        UserRole userAuth = build(userId, ROLE_USER);

        if (!userRoles.contains(anoAuth)) {
            userRoles.add(anoAuth);
        }
        if (!userRoles.contains(userAuth)) {
            userRoles.add(userAuth);
        }
        return userRoles;
    }

    private UserRole build(String userId, String authority) {
        UserRole userRole = new UserRole();
        userRole.setId(IdGenerator.generateId());
        userRole.setUserId(userId);
        userRole.setRoleId(authority);
        return userRole;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByUserId(String userId) {
        userRoleRepository.deleteByUserId(userId);
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
            List<UserRole> newUserRoles = generate(userId, rolesToAdd);
            userRoleRepository.saveAll(newUserRoles);
        }
    }
}
