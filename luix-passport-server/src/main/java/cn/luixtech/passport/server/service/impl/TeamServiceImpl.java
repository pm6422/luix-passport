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
import cn.luixtech.passport.server.service.MailService;
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
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;

@Service
@AllArgsConstructor
@Slf4j
public class TeamServiceImpl implements TeamService {

  private final TeamRepository teamRepository;
  private final TeamUserRepository teamUserRepository;
  private final UserRepository userRepository;
  private final TeamInvitationRepository teamInvitationRepository;
  private final UserAvatarService userAvatarService;
  private final MailService mailService;

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
  public void inviteMembers(HttpServletRequest request, String teamId, List<TeamMemberInvitation> invitations,
      String inviterId) {
    TeamUser inviter = teamUserRepository.findByTeamIdAndUserId(teamId, inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter is not a member of the team"));

    if (!ROLE_OWNER.equals(inviter.getRole())) {
      throw new IllegalArgumentException("Only team owners can invite new members");
    }

    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new IllegalArgumentException("Team not found"));

    User inviterUser = userRepository.findById(inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter user not found"));

    String inviterName = inviterUser.getFirstName() != null && inviterUser.getLastName() != null
        ? inviterUser.getFirstName() + " " + inviterUser.getLastName()
        : inviterUser.getUsername();

    for (TeamMemberInvitation invitation : invitations) {
      Optional<User> existingUser = userRepository.findOneByEmail(invitation.getEmail());

      // Check if user is already a member
      if (existingUser.isPresent()) {
        String userId = existingUser.get().getUsername();
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

      // Send invitation email if user doesn't have an account
      if (existingUser.isEmpty()) {
        try {
          mailService.sendTeamInvitationEmail(
              invitation.getEmail(),
              team.getName(),
              inviterName,
              invitation.getRole(),
              getRequestUrl(request),
              invite.getExpiresAt(),
              inviterUser.getLocale());
          log.info("Sent team invitation email to {} for team {}", invitation.getEmail(), team.getName());
        } catch (Exception e) {
          log.error("Failed to send team invitation email to {}: {}", invitation.getEmail(), e.getMessage());
        }
      }
    }
  }

  @Override
  public List<TeamInvitation> findPendingInvitations(String teamId) {
    List<TeamInvitation> invites = teamInvitationRepository.findByTeamId(teamId);
    invites.forEach(inv -> inv.setRegistered(userRepository.findOneByEmail(inv.getEmail()).isPresent()));
    return invites;
  }

  @Override
  public byte[] getTeamPhoto(String teamId) {
    return teamRepository.findById(teamId)
        .map(Team::getPhoto)
        .orElse(null);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateTeamPhoto(String teamId, byte[] photo) {
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new RuntimeException("Team not found"));
    team.setPhoto(photo);
    teamRepository.save(team);
  }

  @Override
  public List<TeamInvitation> findUserInvitations(String email) {
    return teamInvitationRepository.findByEmailAndStatusAndExpiresAtAfter(email, "PENDING", Instant.now());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void acceptInvitation(String invitationId, String userId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

    if (!"PENDING".equals(invitation.getStatus())) {
      throw new IllegalArgumentException("Invitation is not pending");
    }

    if (invitation.getExpiresAt().isBefore(Instant.now())) {
      throw new IllegalArgumentException("Invitation has expired");
    }

    // Add user to team
    TeamUser teamUser = new TeamUser();
    teamUser.setTeamId(invitation.getTeamId());
    teamUser.setUserId(userId);
    teamUser.setRole(invitation.getRole());
    teamUserRepository.save(teamUser);

    // Update invitation status
    invitation.setStatus("ACCEPTED");
    teamInvitationRepository.save(invitation);

    log.info("User {} accepted invitation {} to join team {}", userId, invitationId, invitation.getTeamId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void rejectInvitation(String invitationId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

    if (!"PENDING".equals(invitation.getStatus())) {
      throw new IllegalArgumentException("Invitation is not pending");
    }

    invitation.setStatus("REJECTED");
    teamInvitationRepository.save(invitation);

    log.info("Invitation {} was rejected", invitationId);
  }
}
