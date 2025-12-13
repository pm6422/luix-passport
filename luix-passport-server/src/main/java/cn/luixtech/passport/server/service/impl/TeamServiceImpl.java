package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Team;
import cn.luixtech.passport.server.domain.TeamInvitation;
import cn.luixtech.passport.server.domain.TeamMember;
import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.pojo.TeamMemberInvitation;
import cn.luixtech.passport.server.repository.TeamInvitationRepository;
import cn.luixtech.passport.server.repository.TeamRepository;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.TeamService;
import cn.luixtech.passport.server.service.UserAvatarService;
import cn.luixtech.passport.server.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
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
  private final TeamInvitationRepository teamInvitationRepository;
  private final UserAvatarService userAvatarService;

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
  public List<TeamMember> findTeamMembers(HttpServletRequest request, String teamId) {
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
      member.setJoinedAt(user.getCreatedAt());

      try {
        member.setAvatar(userAvatarService.getAvatar(user.getUsername(), request));
      } catch (IOException e) {
        throw new RuntimeException(e);
      }
      return member;
    }).filter(Objects::nonNull).collect(Collectors.toList());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void inviteMembers(String teamId, List<TeamMemberInvitation> invitations, String inviterId) {
    TeamUser inviter = teamUserRepository.findByTeamIdAndUserId(teamId, inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter is not a member of the team"));

    if (!ROLE_OWNER.equals(inviter.getRole())) {
      throw new IllegalArgumentException("Only team owners can invite new members");
    }

    for (TeamMemberInvitation invitation : invitations) {
      // Check if user is already a member
      if (userRepository.findOneByEmail(invitation.getEmail()).isPresent()) {
        String userId = userRepository.findOneByEmail(invitation.getEmail()).get().getUsername();
        if (teamUserRepository.findByTeamIdAndUserId(teamId, userId).isPresent()) {
          continue; // Already a member
        }
      }

      // Check for existing pending invitation
      Optional<TeamInvitation> existingInvite = teamInvitationRepository.findByTeamIdAndEmailAndStatus(teamId,
          invitation.getEmail(), "PENDING");
      if (existingInvite.isPresent()) {
        // Update role if needed or just skip
        continue;
      }

      TeamInvitation invite = new TeamInvitation();
      invite.setTeamId(teamId);
      invite.setEmail(invitation.getEmail());
      invite.setRole(invitation.getRole());
      invite.setInviterId(inviterId);
      invite.setStatus("PENDING");
      invite.setCreatedAt(Instant.now());
      invite.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
      teamInvitationRepository.save(invite);
    }
  }

  @Override
  public List<TeamInvitation> findPendingInvitations(String teamId) {
    return teamInvitationRepository.findByTeamIdAndStatus(teamId, "PENDING");
  }
}
