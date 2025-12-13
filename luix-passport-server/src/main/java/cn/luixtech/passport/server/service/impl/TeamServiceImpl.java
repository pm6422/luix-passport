package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Team;
import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.repository.TeamRepository;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.service.TeamService;
import cn.luixtech.passport.server.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import static cn.luixtech.passport.server.domain.TeamUser.ROLE_OWNER;

@Service
@AllArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamUserRepository teamUserRepository;

  @Override
  @Transactional(rollbackFor = Exception.class)
  public Team create(String name, byte[] photo) {
    Team team = new Team();
    team.setName(name);
    team.setPhoto(photo);
    team.setEnabled(true);
    teamRepository.save(team);

    TeamUser teamUser = new TeamUser();
    teamUser.setTeamId(team.getId());
    teamUser.setUserId(AuthUtils.getCurrentUsername());
    teamUser.setRole(ROLE_OWNER);
    teamUserRepository.save(teamUser);

    return team;
  }

  @Override
  public java.util.List<Team> findUserTeams(String username) {
    java.util.List<TeamUser> teamUsers = teamUserRepository.findByUserId(username);
    if (teamUsers.isEmpty()) {
      return java.util.Collections.emptyList();
    }
    java.util.List<String> teamIds = teamUsers.stream()
        .map(TeamUser::getTeamId)
        .collect(java.util.stream.Collectors.toList());
    return teamRepository.findAllById(teamIds);
  }
}
