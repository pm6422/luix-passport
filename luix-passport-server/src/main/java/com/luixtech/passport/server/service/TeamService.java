package com.luixtech.passport.server.service;

import com.luixtech.passport.server.domain.Team;
import com.luixtech.passport.server.domain.TeamInvitation;
import com.luixtech.passport.server.dto.TeamMember;
import com.luixtech.passport.server.dto.TeamMemberInvitation;
import jakarta.servlet.http.HttpServletRequest;

import java.util.List;

public interface TeamService {
  Team create(String name, byte[] photo);

  List<Team> findUserTeams(String username);

  List<TeamMember> findTeamMembers(HttpServletRequest request, String teamId);

  void inviteMembers(HttpServletRequest request, String teamId, List<TeamMemberInvitation> invitations,
      String inviterId);

  List<TeamInvitation> findPendingInvitations(String teamId);

  byte[] getTeamPhoto(String teamId);

  void updateTeamPhoto(String teamId, byte[] photo);

  List<TeamInvitation> findUserInvitations(String email);

  void acceptInvitation(String invitationId, String userId);

  void rejectInvitation(String invitationId);

  void resendInvitation(HttpServletRequest request, String invitationId, String inviterId);

  void updateInviteeRoles(String teamId, String userId, java.util.Set<String> roles, String operatorId);

  void deleteTeam(String teamId);

  void updateTeamName(String teamId, String name, String operatorId);
}
