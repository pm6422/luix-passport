package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.dto.TeamMemberInvitation;
import java.util.List;
import java.util.Set;

public interface TeamUserService {

    Set<String> findTeamIdsByUsername(String username);

    void save(String teamId, String username);

    void save(String teamId, Set<String> usernames);

}
