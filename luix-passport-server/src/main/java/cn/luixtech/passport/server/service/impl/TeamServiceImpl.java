package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Team;
import cn.luixtech.passport.server.domain.TeamInvitation;
import cn.luixtech.passport.server.dto.TeamMember;
import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.dto.TeamMemberInvitation;
import cn.luixtech.passport.server.repository.TeamInvitationRepository;
import cn.luixtech.passport.server.repository.TeamRepository;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.MailService;
import cn.luixtech.passport.server.service.TeamService;
import cn.luixtech.passport.server.service.UserRoleService;
import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.service.UserAvatarService;
import cn.luixtech.passport.server.utils.AuthUtils;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.scheduling.annotation.Scheduled;
import net.javacrumbs.shedlock.spring.annotation.SchedulerLock;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.*;
import java.util.function.Function;
import java.util.stream.Collectors;

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
  private final UserRoleService userRoleService;

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
    teamUserRepository.save(teamUser);

    Set<String> currentRoles = userRoleService.findRoleIds(AuthUtils.getCurrentUsername());
    Set<String> updatedRoles = new java.util.HashSet<>(currentRoles);
    updatedRoles.add(Role.TEAM_OWNER);
    userRoleService.update(AuthUtils.getCurrentUsername(), updatedRoles);

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
    String currentUser = AuthUtils.getCurrentUsername();
    if (teamUserRepository.findByTeamIdAndUserId(teamId, currentUser).isEmpty()) {
      throw new IllegalArgumentException("Not a team member");
    }
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
      Set<String> roleIds = userRoleService.findRoleIds(user.getUsername());
      member.setRole(roleIds.contains(Role.TEAM_OWNER) ? Role.TEAM_OWNER : Role.TEAM_MEMBER);
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

    Set<String> inviterRoleIds = userRoleService.findRoleIds(inviterId);
    if (!inviterRoleIds.contains(Role.TEAM_OWNER)) {
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

      // Check for existing invitation (unregistered or registered and not finalized)
      Optional<TeamInvitation> existingUnregistered = teamInvitationRepository.findByTeamIdAndMemberEmailAndStatus(teamId,
          invitation.getEmail(), TeamInvitation.STATUS_UNREGISTERED);
      Optional<TeamInvitation> existingRegistered = teamInvitationRepository.findByTeamIdAndMemberEmailAndStatus(teamId,
          invitation.getEmail(), TeamInvitation.STATUS_REGISTERED);
      if (existingUnregistered.isPresent() || existingRegistered.isPresent()) {
        // Update role if needed or just skip
        continue;
      }

      TeamInvitation invite = new TeamInvitation();
      invite.setTeamId(teamId);
      invite.setMemberEmail(invitation.getEmail());
      invite.setMemberRole(invitation.getRole());
      invite.setInviterId(inviterId);
      invite.setStatus(existingUser.isPresent() ? TeamInvitation.STATUS_REGISTERED : TeamInvitation.STATUS_UNREGISTERED);
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
    String currentUser = AuthUtils.getCurrentUsername();
    TeamUser operator = teamUserRepository.findByTeamIdAndUserId(teamId, currentUser)
        .orElseThrow(() -> new IllegalArgumentException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(currentUser);
    if (!operatorRoleIds.contains(Role.TEAM_OWNER)) {
      throw new IllegalArgumentException("Only team owners can view pending invitations");
    }
    List<TeamInvitation> invites = teamInvitationRepository.findByTeamId(teamId);
    Instant now = Instant.now();
    for (TeamInvitation inv : invites) {
      if (!TeamInvitation.STATUS_ACCEPTED.equals(inv.getStatus())
          && !TeamInvitation.STATUS_REJECTED.equals(inv.getStatus())) {
        if (inv.getExpiresAt().isBefore(now)) {
          if (!TeamInvitation.STATUS_EXPIRED.equals(inv.getStatus())) {
            inv.setStatus(TeamInvitation.STATUS_EXPIRED);
            teamInvitationRepository.save(inv);
          }
        } else {
          boolean hasAccount = userRepository.findOneByEmail(inv.getMemberEmail()).isPresent();
          if (hasAccount && TeamInvitation.STATUS_UNREGISTERED.equals(inv.getStatus())) {
            inv.setStatus(TeamInvitation.STATUS_REGISTERED);
            teamInvitationRepository.save(inv);
          }
        }
      }
    }
    return invites.stream()
        .filter(inv -> !TeamInvitation.STATUS_ACCEPTED.equals(inv.getStatus()))
        .collect(Collectors.toList());
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
    String currentUser = AuthUtils.getCurrentUsername();
    TeamUser operator = teamUserRepository.findByTeamIdAndUserId(teamId, currentUser)
        .orElseThrow(() -> new RuntimeException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(currentUser);
    if (!operatorRoleIds.contains(Role.TEAM_OWNER)) {
      throw new RuntimeException("Only team owners can update team photo");
    }
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new RuntimeException("Team not found"));
    team.setPhoto(photo);
    teamRepository.save(team);
  }

  @Override
  public List<TeamInvitation> findUserInvitations(String email) {
    return teamInvitationRepository.findByMemberEmailAndStatusAndExpiresAtAfter(email, TeamInvitation.STATUS_REGISTERED, Instant.now());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void acceptInvitation(String invitationId, String userId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

    if (!TeamInvitation.STATUS_REGISTERED.equals(invitation.getStatus())) {
      throw new IllegalArgumentException("Invitation is not available for acceptance");
    }

    if (invitation.getExpiresAt().isBefore(Instant.now())) {
      throw new IllegalArgumentException("Invitation has expired");
    }

    // Add user to team
    TeamUser teamUser = new TeamUser();
    teamUser.setTeamId(invitation.getTeamId());
    teamUser.setUserId(userId);
    teamUserRepository.save(teamUser);

    Set<String> currentRoles = userRoleService.findRoleIds(userId);
    Set<String> updatedRoles = new java.util.HashSet<>(currentRoles);
    updatedRoles.add(invitation.getMemberRole());
    userRoleService.update(userId, updatedRoles);

    // Update invitation status
    invitation.setStatus(TeamInvitation.STATUS_ACCEPTED);
    teamInvitationRepository.save(invitation);

    log.info("User {} accepted invitation {} to join team {}", userId, invitationId, invitation.getTeamId());
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void rejectInvitation(String invitationId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

    if (!TeamInvitation.STATUS_REGISTERED.equals(invitation.getStatus())) {
      throw new IllegalArgumentException("Invitation is not available for rejection");
    }

    invitation.setStatus(TeamInvitation.STATUS_REJECTED);
    teamInvitationRepository.save(invitation);

    log.info("Invitation {} was rejected", invitationId);
  }

  @Scheduled(cron = "0 0 0 * * ?")
  @SchedulerLock(name = "expireTeamInvitations")
  @Transactional(rollbackFor = Exception.class)
  public void expireTeamInvitationsJob() {
    Instant now = Instant.now();
    List<String> exclude = Arrays.asList(
        TeamInvitation.STATUS_ACCEPTED,
        TeamInvitation.STATUS_REJECTED,
        TeamInvitation.STATUS_EXPIRED
    );
    List<TeamInvitation> toExpire = teamInvitationRepository.findByExpiresAtBeforeAndStatusNotIn(now, exclude);
    if (toExpire.isEmpty()) {
      return;
    }
    for (TeamInvitation inv : toExpire) {
      inv.setStatus(TeamInvitation.STATUS_EXPIRED);
    }
    teamInvitationRepository.saveAll(toExpire);
    log.info("Expired {} team invitations", toExpire.size());
  }

  @Transactional(rollbackFor = Exception.class)
  public void resendInvitation(HttpServletRequest request, String invitationId, String inviterId) {
    TeamInvitation invitation = teamInvitationRepository.findById(invitationId)
        .orElseThrow(() -> new IllegalArgumentException("Invitation not found"));

    if (!TeamInvitation.STATUS_EXPIRED.equals(invitation.getStatus())) {
      throw new IllegalArgumentException("Only expired invitations can be resent");
    }

    TeamUser inviter = teamUserRepository.findByTeamIdAndUserId(invitation.getTeamId(), inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter is not a member of the team"));
    Set<String> inviterRoleIds = userRoleService.findRoleIds(inviterId);
    if (!inviterRoleIds.contains(Role.TEAM_OWNER)) {
      throw new IllegalArgumentException("Only team owners can resend invitations");
    }

    User inviterUser = userRepository.findById(inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter user not found"));
    String inviterName = inviterUser.getFirstName() != null && inviterUser.getLastName() != null
        ? inviterUser.getFirstName() + " " + inviterUser.getLastName()
        : inviterUser.getUsername();

    boolean hasAccount = userRepository.findOneByEmail(invitation.getMemberEmail()).isPresent();
    invitation.setStatus(hasAccount ? TeamInvitation.STATUS_REGISTERED : TeamInvitation.STATUS_UNREGISTERED);
    invitation.setCreatedAt(Instant.now());
    invitation.setExpiresAt(Instant.now().plus(7, ChronoUnit.DAYS));
    teamInvitationRepository.save(invitation);

    if (!hasAccount) {
      try {
        mailService.sendTeamInvitationEmail(
            invitation.getMemberEmail(),
            teamRepository.findById(invitation.getTeamId()).map(Team::getName)
                .orElse(""),
            inviterName,
            invitation.getMemberRole(),
            getRequestUrl(request),
            invitation.getExpiresAt(),
            inviterUser.getLocale());
        log.info("Resent invitation email to {}", invitation.getMemberEmail());
      } catch (Exception e) {
        log.error("Failed to resend team invitation email to {}: {}", invitation.getMemberEmail(), e.getMessage());
      }
    }
  }

  // batch operations removed

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateMemberRole(String teamId, String userId, String role, String operatorId) {
    if (!Role.TEAM_OWNER.equals(role) && !Role.TEAM_MEMBER.equals(role)) {
      throw new IllegalArgumentException("Invalid role");
    }
    TeamUser operator = teamUserRepository.findByTeamIdAndUserId(teamId, operatorId)
        .orElseThrow(() -> new IllegalArgumentException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(operatorId);
    if (!operatorRoleIds.contains(Role.TEAM_OWNER)) {
      throw new IllegalArgumentException("Only team owners can edit member roles");
    }
    if (operatorId.equals(userId) && !Role.TEAM_OWNER.equals(role)) {
      throw new IllegalArgumentException("Owners cannot change their own role");
    }
    teamUserRepository.findByTeamIdAndUserId(teamId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Target user is not a member of the team"));
    Set<String> targetRoleIds = userRoleService.findRoleIds(userId);
    boolean targetIsOwner = targetRoleIds.contains(Role.TEAM_OWNER);
    if (targetIsOwner && !Role.TEAM_OWNER.equals(role)) {
      long ownerCount = teamUserRepository.findByTeamId(teamId).stream().map(TeamUser::getUserId)
          .filter(uid -> userRoleService.findRoleIds(uid).contains(Role.TEAM_OWNER))
          .count();
      if (ownerCount <= 1) {
        throw new IllegalArgumentException("At least one team owner must remain");
      }
    }
    Set<String> newRoles = new java.util.HashSet<>(targetRoleIds);
    if (Role.TEAM_OWNER.equals(role)) {
      newRoles.add(Role.TEAM_OWNER);
      newRoles.remove(Role.TEAM_MEMBER);
    } else {
      newRoles.remove(Role.TEAM_OWNER);
      newRoles.add(Role.TEAM_MEMBER);
    }
    userRoleService.update(userId, newRoles);
  }
}
