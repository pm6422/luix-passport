package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserRole;

import java.util.List;
import java.util.Set;

public interface UserRoleService {

    Set<String> findRoleIds(String userId);

    List<UserRole> generate(String userId, Set<String> authorities);

    void deleteByUserId(String userId);

    void update(String userId, Set<String> roles);
}
