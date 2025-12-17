package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.annotation.SchedulerLog;
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
    updatedRoles.add(Role.ROLE_TEAM_OWNER);
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
      member.setRoles(roleIds);
      member.setRole(roleIds.contains(Role.ROLE_TEAM_OWNER) ? Role.ROLE_TEAM_OWNER : Role.ROLE_TEAM_MEMBER);
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
    teamUserRepository.findByTeamIdAndUserId(teamId, inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter is not a member of the team"));

    Set<String> inviterRoleIds = userRoleService.findRoleIds(inviterId);
    if (!inviterRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
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
      Optional<User> existingUser = userRepository.findOneByEmail(invitation.getInviteeEmail());

      // Check if user is already registered
      if (existingUser.isPresent()) {
        throw new IllegalArgumentException("The email " + invitation.getInviteeEmail() + " is already registered");
      }

      // Check for existing invitation (unregistered or registered and not finalized)
      Optional<TeamInvitation> existingUnregistered = teamInvitationRepository.findByTeamIdAndInviteeEmailAndStatus(
          teamId,
          invitation.getInviteeEmail(), TeamInvitation.STATUS_UNREGISTERED);
      Optional<TeamInvitation> existingRegistered = teamInvitationRepository.findByTeamIdAndInviteeEmailAndStatus(
          teamId,
          invitation.getInviteeEmail(), TeamInvitation.STATUS_REGISTERED);
      if (existingUnregistered.isPresent() || existingRegistered.isPresent()) {
        // Update role if needed or just skip
        continue;
      }

      TeamInvitation invite = new TeamInvitation();
      invite.setTeamId(teamId);
      invite.setInviteeEmail(invitation.getInviteeEmail());
      invite.setInviteeRoles(String.join(",", invitation.getInviteeRoles()));
      invite.setInviterId(inviterId);
      invite
          .setStatus(existingUser.isPresent() ? TeamInvitation.STATUS_REGISTERED : TeamInvitation.STATUS_UNREGISTERED);
      invite.setCreatedAt(Instant.now());
      invite.setExpiresAt(Instant.now().plus(TeamInvitation.INVITATION_EXPIRATION_DAYS, ChronoUnit.DAYS));
      teamInvitationRepository.save(invite);

      // Send invitation email if user doesn't have an account
      if (existingUser.isEmpty()) {
        try {
          User invitee = new User();
          invitee.setEmail(invitation.getInviteeEmail());
          // Set the locale of the invitee to the same as the inviter, so the email is
          // sent in the correct language
          invitee.setLocale(inviterUser.getLocale());
          mailService.sendTeamInvitationEmail(
              invitee,
              getRequestUrl(request),
              team.getName(),
              inviterName);
          log.info("Sent team invitation email to {} for team {}", invitation.getInviteeEmail(), team.getName());
        } catch (Exception e) {
          log.error("Failed to send team invitation email to {}: {}", invitation.getInviteeEmail(), e.getMessage());
        }
      }
    }
  }

  @Override
  public List<TeamInvitation> findPendingInvitations(String teamId) {
    String currentUser = AuthUtils.getCurrentUsername();
    teamUserRepository.findByTeamIdAndUserId(teamId, currentUser)
        .orElseThrow(() -> new IllegalArgumentException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(currentUser);
    if (!operatorRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
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
          boolean hasAccount = userRepository.findOneByEmail(inv.getInviteeEmail()).isPresent();
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
    teamUserRepository.findByTeamIdAndUserId(teamId, currentUser)
        .orElseThrow(() -> new RuntimeException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(currentUser);
    if (!operatorRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
      throw new RuntimeException("Only team owners can update team photo");
    }
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new RuntimeException("Team not found"));
    team.setPhoto(photo);
    teamRepository.save(team);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateTeamName(String teamId, String name, String operatorId) {
    teamUserRepository.findByTeamIdAndUserId(teamId, operatorId)
        .orElseThrow(() -> new RuntimeException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(operatorId);
    if (!operatorRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
      throw new RuntimeException("Only team owners can update team name");
    }
    Team team = teamRepository.findById(teamId)
        .orElseThrow(() -> new RuntimeException("Team not found"));
    team.setName(name);
    teamRepository.save(team);
  }

  @Override
  public List<TeamInvitation> findUserInvitations(String email) {
    return teamInvitationRepository.findByInviteeEmailAndStatusAndExpiresAtAfter(email,
        TeamInvitation.STATUS_REGISTERED,
        Instant.now());
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
    if (invitation.getInviteeRoles() != null) {
      updatedRoles.addAll(Arrays.asList(invitation.getInviteeRoles().split(",")));
    }
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
  @SchedulerLog(name = "expireTeamInvitations")
  @Transactional(rollbackFor = Exception.class)
  public void expireTeamInvitationsJob() {
    Instant now = Instant.now();
    List<String> exclude = Arrays.asList(
        TeamInvitation.STATUS_ACCEPTED,
        TeamInvitation.STATUS_REJECTED,
        TeamInvitation.STATUS_EXPIRED);
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

    teamUserRepository.findByTeamIdAndUserId(invitation.getTeamId(), inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter is not a member of the team"));
    Set<String> inviterRoleIds = userRoleService.findRoleIds(inviterId);
    if (!inviterRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
      throw new IllegalArgumentException("Only team owners can resend invitations");
    }

    User inviterUser = userRepository.findById(inviterId)
        .orElseThrow(() -> new IllegalArgumentException("Inviter user not found"));
    String inviterName = inviterUser.getFirstName() != null && inviterUser.getLastName() != null
        ? inviterUser.getFirstName() + " " + inviterUser.getLastName()
        : inviterUser.getUsername();

    boolean hasAccount = userRepository.findOneByEmail(invitation.getInviteeEmail()).isPresent();
    invitation.setStatus(hasAccount ? TeamInvitation.STATUS_REGISTERED : TeamInvitation.STATUS_UNREGISTERED);
    invitation.setCreatedAt(Instant.now());
    invitation.setExpiresAt(Instant.now().plus(TeamInvitation.INVITATION_EXPIRATION_DAYS, ChronoUnit.DAYS));
    teamInvitationRepository.save(invitation);

    if (!hasAccount) {
      try {
        User invitee = new User();
        invitee.setEmail(invitation.getInviteeEmail());
        mailService.sendTeamInvitationEmail(
            invitee,
            getRequestUrl(request),
            teamRepository.findById(invitation.getTeamId()).map(Team::getName).orElse(""),
            inviterName);
        log.info("Resent invitation email to {}", invitation.getInviteeEmail());
      } catch (Exception e) {
        log.error("Failed to resend team invitation email to {}: {}", invitation.getInviteeEmail(), e.getMessage());
      }
    }
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void updateInviteeRoles(String teamId, String userId, Set<String> roles, String operatorId) {
    // Ensure mutually exclusive roles logic (frontend should handle this, but
    // backend must validate)
    // Actually, user said only one of TEAM_OWNER / TEAM_MEMBER can be selected.
    if (roles.contains(Role.ROLE_TEAM_OWNER) && roles.contains(Role.ROLE_TEAM_MEMBER)) {
      throw new IllegalArgumentException("Cannot be both Team Owner and Team Member");
    }
    if (!roles.contains(Role.ROLE_TEAM_OWNER) && !roles.contains(Role.ROLE_TEAM_MEMBER)) {
      // Must have one
      throw new IllegalArgumentException("Must have either Team Owner or Team Member role");
    }

    teamUserRepository.findByTeamIdAndUserId(teamId, operatorId)
        .orElseThrow(() -> new IllegalArgumentException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(operatorId);
    if (!operatorRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
      throw new IllegalArgumentException("Only team owners can edit member roles");
    }

    // Check if operator is modifying themselves
    if (operatorId.equals(userId)) {
      // If operator is removing their own owner role
      if (!roles.contains(Role.ROLE_TEAM_OWNER)) {
        throw new IllegalArgumentException("Owners cannot remove their own owner role");
      }
    }

    teamUserRepository.findByTeamIdAndUserId(teamId, userId)
        .orElseThrow(() -> new IllegalArgumentException("Target user is not a member of the team"));
    Set<String> targetRoleIds = userRoleService.findRoleIds(userId);
    boolean targetWasOwner = targetRoleIds.contains(Role.ROLE_TEAM_OWNER);
    boolean targetIsOwner = roles.contains(Role.ROLE_TEAM_OWNER);

    if (targetWasOwner && !targetIsOwner) {
      long ownerCount = teamUserRepository.findByTeamId(teamId).stream().map(TeamUser::getUserId)
          .filter(uid -> userRoleService.findRoleIds(uid).contains(Role.ROLE_TEAM_OWNER))
          .count();
      if (ownerCount <= 1) {
        throw new IllegalArgumentException("At least one team owner must remain");
      }
    }

    userRoleService.update(userId, roles);
  }

  @Override
  @Transactional(rollbackFor = Exception.class)
  public void deleteTeam(String teamId) {
    String operatorId = AuthUtils.getCurrentUsername();
    teamUserRepository.findByTeamIdAndUserId(teamId, operatorId)
        .orElseThrow(() -> new IllegalArgumentException("Operator is not a member of the team"));
    Set<String> operatorRoleIds = userRoleService.findRoleIds(operatorId);
    if (!operatorRoleIds.contains(Role.ROLE_TEAM_OWNER)) {
      throw new IllegalArgumentException("Only team owners can delete team");
    }
    List<TeamInvitation> invites = teamInvitationRepository.findByTeamId(teamId);
    if (!invites.isEmpty()) {
      teamInvitationRepository.deleteAll(invites);
    }
    teamUserRepository.deleteByTeamId(teamId);
    teamRepository.deleteById(teamId);
    log.info("Team {} deleted by {}", teamId, operatorId);
  }
}
