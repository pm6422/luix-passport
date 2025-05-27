package cn.luixtech.passport.server.service;

import java.util.Set;

public interface OrgUserService {

    Set<String> findOrgIdsByUsername(String username);

    void save(String orgId, Set<String> usernames);

    void save(String orgId, String username);
}
