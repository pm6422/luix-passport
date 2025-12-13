package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.repository.TeamRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;

/**
 * REST controller for managing teams.
 */
import cn.luixtech.passport.server.service.TeamService;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import java.io.IOException;

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
    public ResponseEntity<java.util.List<cn.luixtech.passport.server.domain.Team>> findUserTeams() {
        return ResponseEntity
                .ok(teamService.findUserTeams(cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername()));
    }
}
