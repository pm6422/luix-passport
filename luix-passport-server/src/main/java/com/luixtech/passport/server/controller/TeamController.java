package com.luixtech.passport.server.controller;

import com.luixtech.passport.server.repository.TeamRepository;
import com.luixtech.passport.server.repository.UserRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

import com.luixtech.passport.server.domain.Role;
import com.luixtech.passport.server.domain.Team;
import com.luixtech.passport.server.domain.TeamInvitation;
import com.luixtech.passport.server.dto.TeamMember;
import com.luixtech.passport.server.dto.TeamMemberInvitation;
import com.luixtech.passport.server.service.TeamService;
import com.luixtech.passport.server.service.RoleService;
import com.luixtech.passport.server.utils.AuthUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.Set;

import static com.luixtech.passport.server.domain.Role.ROLE_ADMIN;
import org.springframework.security.access.prepost.PreAuthorize;

/**
 * REST controller for managing teams.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class TeamController {
    private final TeamRepository teamRepository;
    private final TeamService teamService;
    private final RoleService roleService;
    private final UserRepository userRepository;

    @Operation(summary = "count enabled teams")
    @GetMapping("/api/teams/count")
    public ResponseEntity<Long> count(@RequestParam(required = false) java.time.Instant since) {
        if (since != null) {
            return ResponseEntity.ok(teamRepository.countByCreatedAtAfter(since));
        }
        return ResponseEntity.ok(teamRepository.count());
    }

    @Operation(summary = "get membership statistics")
    @GetMapping("/api/teams/membership-stats")
    @PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
    public ResponseEntity<Map<String, Long>> getMembershipStats() {
        Map<String, Long> membershipStats = new HashMap<>();
        membershipStats.put("FREE", userRepository.countByMembershipTypeIsNull());
        membershipStats.put("PRO",
                userRepository.countByMembershipType(Role.ROLE_MEMBER_PRO));
        membershipStats.put("PREMIUM",
                userRepository.countByMembershipType(Role.ROLE_MEMBER_PREMIUM));
        return ResponseEntity.ok(membershipStats);
    }

    @Operation(summary = "find all role IDs")
    @GetMapping("/api/teams/roles/ids")
    public ResponseEntity<Set<String>> findAllRoleIds() {
        return ResponseEntity.ok(roleService.findAllIds());
    }

    @Operation(summary = "create team")
    @PostMapping("/api/teams")
    public ResponseEntity<Team> create(@RequestParam String name, @RequestParam(required = false) MultipartFile photo)
            throws IOException {
        byte[] photoBytes = null;
        if (photo != null && !photo.isEmpty()) {
            photoBytes = photo.getBytes();
        }
        Team team = teamService.create(name, photoBytes);
        return ResponseEntity.ok(team);
    }

    @Operation(summary = "get user teams")
    @GetMapping("/api/teams")
    public ResponseEntity<List<Team>> findUserTeams() {
        return ResponseEntity
                .ok(teamService.findUserTeams(AuthUtils.getCurrentUsername()));
    }

    @Operation(summary = "get team members")
    @GetMapping("/api/teams/{teamId}/members")
    public ResponseEntity<List<TeamMember>> getMembers(HttpServletRequest request, @PathVariable String teamId) {
        return ResponseEntity.ok(teamService.findTeamMembers(request, teamId));
    }

    @Operation(summary = "invite team members")
    @PostMapping("/api/teams/{teamId}/members")
    public ResponseEntity<Void> inviteMembers(HttpServletRequest request, @PathVariable String teamId,
            @RequestBody List<TeamMemberInvitation> invitations) {
        teamService.inviteMembers(request, teamId, invitations, AuthUtils.getCurrentUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "delete team")
    @DeleteMapping("/api/teams/{teamId}")
    public ResponseEntity<Void> delete(@PathVariable String teamId) {
        teamService.deleteTeam(teamId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get pending invitations")
    @GetMapping("/api/teams/{teamId}/invitations")
    public ResponseEntity<List<TeamInvitation>> getPendingInvitations(@PathVariable String teamId) {
        return ResponseEntity.ok(teamService.findPendingInvitations(teamId));
    }

    @Operation(summary = "get team photo")
    @GetMapping("/api/teams/{teamId}/photo")
    public ResponseEntity<byte[]> getTeamPhoto(@PathVariable String teamId) {
        byte[] photo = teamService.getTeamPhoto(teamId);
        if (photo == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok()
                .header("Content-Type", "image/png")
                .body(photo);
    }

    @Operation(summary = "update team photo")
    @PutMapping("/api/teams/{teamId}/photo")
    public ResponseEntity<Void> updateTeamPhoto(@PathVariable String teamId,
            @RequestParam MultipartFile photo) throws IOException {
        teamService.updateTeamPhoto(teamId, photo.getBytes());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "update team name")
    @PutMapping("/api/teams/{teamId}/name")
    public ResponseEntity<Void> updateTeamName(@PathVariable String teamId, @RequestParam String name) {
        teamService.updateTeamName(teamId, name, AuthUtils.getCurrentUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get user invitations")
    @GetMapping("/api/user/invitations")
    public ResponseEntity<List<TeamInvitation>> getUserInvitations() {
        String email = AuthUtils.getCurrentUserEmail();
        return ResponseEntity.ok(teamService.findUserInvitations(email));
    }

    @Operation(summary = "accept team invitation")
    @PostMapping("/api/invitations/{invitationId}/accept")
    public ResponseEntity<Void> acceptInvitation(@PathVariable String invitationId) {
        String userId = AuthUtils.getCurrentUsername();
        teamService.acceptInvitation(invitationId, userId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "reject team invitation")
    @PostMapping("/api/invitations/{invitationId}/reject")
    public ResponseEntity<Void> rejectInvitation(@PathVariable String invitationId) {
        teamService.rejectInvitation(invitationId);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "resend team invitation")
    @PostMapping("/api/invitations/{invitationId}/resend")
    public ResponseEntity<Void> resendInvitation(HttpServletRequest request, @PathVariable String invitationId) {
        teamService.resendInvitation(request, invitationId, AuthUtils.getCurrentUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "update team member roles")
    @PutMapping("/api/teams/{teamId}/members/{userId}/role")
    public ResponseEntity<Void> updateMemberRole(@PathVariable String teamId, @PathVariable String userId,
            @RequestBody java.util.Set<String> roles) {
        teamService.updateInviteeRoles(teamId, userId, roles, AuthUtils.getCurrentUsername());
        return ResponseEntity.ok().build();
    }

}
