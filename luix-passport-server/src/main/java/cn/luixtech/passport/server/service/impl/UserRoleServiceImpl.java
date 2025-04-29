package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.UserRole;
import cn.luixtech.passport.server.repository.UserRoleRepository;
import cn.luixtech.passport.server.service.UserRoleService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ANONYMOUS;
import static cn.luixtech.passport.server.domain.UserRole.ROLE_USER;

@Service
@AllArgsConstructor
public class UserRoleServiceImpl implements UserRoleService {

    private final UserRoleRepository userRoleRepository;

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
        UserRole userAuthority = new UserRole();
        userAuthority.setUserId(userId);
        userAuthority.setRole(authority);
        return userAuthority;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteByUserId(String userId) {
        userRoleRepository.deleteByUserId(userId);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void update(String userId, Set<String> roles) {
        if (CollectionUtils.isEmpty(roles)) {
            return;
        }
        // first delete user authorities
        deleteByUserId(userId);

        // then insert user authorities
        List<UserRole> userRoles = generate(userId, roles);
        userRoleRepository.saveAll(userRoles);
    }
}
