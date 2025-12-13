package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Team;
import cn.luixtech.passport.server.domain.TeamMember;
import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.repository.TeamRepository;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.TeamService;
import cn.luixtech.passport.server.utils.AuthUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.Function;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.domain.TeamUser.ROLE_OWNER;

@Service
@AllArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamUserRepository teamUserRepository;
  private final UserRepository userRepository;
  private final UserProfilePicRepository userProfilePicRepository;

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
  public List<Team> findUserTeams(String username) {
    List<TeamUser> teamUsers = teamUserRepository.findByUserId(username);
    if (teamUsers.isEmpty()) {
      return Collections.emptyList();
    }
    List<String> teamIds = teamUsers.stream()
        .map(TeamUser::getTeamId)
        .collect(Collectors.toList());
    return teamRepository.findAllById(teamIds);
  }

  @Override
  public List<TeamMember> findTeamMembers(String teamId) {
    List<TeamUser> teamUsers = teamUserRepository.findByTeamId(teamId);
    if (teamUsers.isEmpty()) {
      return Collections.emptyList();
    }

    List<String> userIds = teamUsers.stream()
        .map(TeamUser::getUserId)
        .collect(Collectors.toList());

    Map<String, User> userMap = userRepository.findAllById(userIds)
        .stream()
        .collect(Collectors.toMap(User::getUsername,
            Function.identity()));

    Map<String, UserProfilePic> userProfilePicMap = userProfilePicRepository
        .findAllById(userIds).stream()
        .collect(Collectors.toMap(UserProfilePic::getId,
            Function.identity()));

    return teamUsers.stream().map(teamUser -> {
      User user = userMap.get(teamUser.getUserId());
      if (user == null) {
        return null;
      }
      TeamMember member = new TeamMember();
      member.setUserId(user.getUsername());
      member.setEmail(user.getEmail());
      member.setFirstName(user.getFirstName());
      member.setLastName(user.getLastName());
      member.setRole(teamUser.getRole());
      // team_user table doesn't have created_at mapped in entity yet but database has
      // it.
      // For now we can skip joinedAt or add it to TeamUser entity if needed.

      UserProfilePic pic = userProfilePicMap.get(user.getUsername());
      if (pic != null) {
        member.setProfilePic(pic.getProfilePic());
      }
      return member;
    }).filter(Objects::nonNull).collect(Collectors.toList());
  }
}
