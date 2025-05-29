package cn.luixtech.passport.server.service;

import java.util.Set;

public interface TeamUserService {

    Set<String> findTeamIdsByUsername(String username);

    void save(String teamId, String username);

    void save(String teamId, Set<String> usernames);
}
