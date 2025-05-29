package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.service.TeamUserService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;

import static cn.luixtech.passport.server.persistence.Tables.TEAM_USER;

@Service
@AllArgsConstructor
public class TeamUserServiceImpl implements TeamUserService {

    private       TeamUserRepository teamUserRepository;
    private final DSLContext         dslContext;

    @Override
    public Set<String> findTeamIdsByUsername(String username) {
        return dslContext.select(TEAM_USER.TEAM_ID)
                .from(TEAM_USER)
                .where(TEAM_USER.USERNAME.eq(username))
                .fetchSet(TEAM_USER.TEAM_ID);
    }

    @Override
    public void save(String teamId, String username) {
        save(teamId, Set.of(username));
    }

    @Override
    public void save(String teamId, Set<String> usernames) {
        List<TeamUser> teamUsers = teamUserRepository.findByTeamId(teamId);
        if (CollectionUtils.isEmpty(teamUsers)) {
            // insert
            usernames.forEach(username -> {
                TeamUser teamUser = new TeamUser(teamId, username);
                teamUserRepository.save(teamUser);
            });
        } else {
            // delete
            teamUserRepository.deleteByTeamId(teamId);
            // insert
            usernames.forEach(username -> {
                TeamUser teamUser = new TeamUser(teamId, username);
                teamUserRepository.save(teamUser);
            });
        }
    }
}
