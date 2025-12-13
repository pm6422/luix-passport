package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.TeamUser;
import cn.luixtech.passport.server.pojo.TeamMemberInvitation;
import cn.luixtech.passport.server.repository.TeamUserRepository;
import cn.luixtech.passport.server.service.TeamUserService;
import cn.luixtech.passport.server.utils.AuthUtils;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Optional;

@RestController
@AllArgsConstructor
@Slf4j
public class TeamMemberController {

  private final TeamUserService teamUserService;
  private final TeamUserRepository teamUserRepository;

  @PostMapping("/api/teams/{teamId}/members")
  public ResponseEntity<Void> invite(@PathVariable String teamId,
      @RequestBody @Valid List<TeamMemberInvitation> invitations) {
    String currentUser = AuthUtils.getCurrentUsername();
    Optional<TeamUser> teamUser = teamUserRepository.findByTeamIdAndUserId(teamId, currentUser);
    if (teamUser.isEmpty() || !TeamUser.ROLE_OWNER.equals(teamUser.get().getRole())) {
      return ResponseEntity.status(403).build();
    }

    teamUserService.invite(teamId, invitations);
    return ResponseEntity.ok().build();
  }
}
