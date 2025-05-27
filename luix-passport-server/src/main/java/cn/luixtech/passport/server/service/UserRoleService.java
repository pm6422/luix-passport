package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.UserRole;

import java.util.List;
import java.util.Set;

public interface UserRoleService {

    Set<String> findRoleIds(String username);

    void update(String username, Set<String> roleIds);

    List<UserRole> assignWithDefaults(String username, Set<String> roleIds);

    void deleteByUsername(String username);
}
