package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.SpringSession;
import cn.luixtech.passport.server.repository.SpringSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;

/**
 * REST controller for managing spring sessions.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class SpringSessionController {
    private final SpringSessionRepository springSessionRepository;

    @Operation(summary = "find spring sessions list")
    @GetMapping("/api/spring-sessions")
    public ResponseEntity<List<SpringSession>> find() {
        List<SpringSession> springSessions = springSessionRepository.findAllByOrderByLastAccessTimeDesc();
        return ResponseEntity.ok().body(springSessions);
    }
}
