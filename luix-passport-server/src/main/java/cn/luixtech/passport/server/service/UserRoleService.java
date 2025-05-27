package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.UserRole;

import java.util.List;
import java.util.Set;

public interface UserRoleService {

    Set<String> findRoleIds(String userId);

    void update(String userId, Set<String> roleIds);

    List<UserRole> assignWithDefaults(String userId, Set<String> roleIds);
}
