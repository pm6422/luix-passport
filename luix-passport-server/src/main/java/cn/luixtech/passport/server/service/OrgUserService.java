package cn.luixtech.passport.server.service;

import java.util.Set;

public interface OrgUserService {

    Set<String> findOrgIdsByUserId(String userId);

    void save(String orgId, Set<String> userIds);

    void save(String orgId, String userId);
}
