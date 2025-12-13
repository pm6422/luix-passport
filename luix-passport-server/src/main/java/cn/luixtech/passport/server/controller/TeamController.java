package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

/**
 * REST controller for managing teams.
 */
import cn.luixtech.passport.server.domain.Team;
import cn.luixtech.passport.server.domain.TeamInvitation;
import cn.luixtech.passport.server.domain.TeamMember;
import cn.luixtech.passport.server.pojo.TeamMemberInvitation;
import cn.luixtech.passport.server.service.TeamService;
import cn.luixtech.passport.server.utils.AuthUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;
import java.util.List;

@RestController
@AllArgsConstructor
@Slf4j
public class TeamController {
    private final TeamRepository teamRepository;
    private final TeamService teamService;

    @Operation(summary = "count enabled teams")
    @GetMapping("/api/teams/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(teamRepository.count());
    }

    @Operation(summary = "create team")
    @PostMapping("/api/teams")
    public ResponseEntity<Void> create(@RequestParam String name, @RequestParam(required = false) MultipartFile photo)
            throws IOException {
        byte[] photoBytes = null;
        if (photo != null && !photo.isEmpty()) {
            photoBytes = photo.getBytes();
        }
        teamService.create(name, photoBytes);
        return ResponseEntity.ok().build();
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
    public ResponseEntity<Void> inviteMembers(@PathVariable String teamId,
            @RequestBody List<TeamMemberInvitation> invitations) {
        teamService.inviteMembers(teamId, invitations, AuthUtils.getCurrentUsername());
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "get pending invitations")
    @GetMapping("/api/teams/{teamId}/invitations")
    public ResponseEntity<List<TeamInvitation>> getPendingInvitations(@PathVariable String teamId) {
        return ResponseEntity.ok(teamService.findPendingInvitations(teamId));
    }
}
