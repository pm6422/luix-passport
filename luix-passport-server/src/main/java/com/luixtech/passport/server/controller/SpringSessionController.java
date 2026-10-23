package com.luixtech.passport.server.controller;

import com.luixtech.passport.server.domain.SpringSession;
import com.luixtech.passport.server.repository.SpringSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.luixtech.passport.server.domain.Role.ROLE_ADMIN;

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

    @Operation(summary = "count spring sessions")
    @GetMapping("/api/spring-sessions/count")
    public ResponseEntity<Long> count(@RequestParam(value = "since", required = false) Long since) {
        if (since != null) {
            return ResponseEntity.ok(springSessionRepository.countByCreationTimeAfter(since));
        }
        return ResponseEntity.ok(springSessionRepository.count());
    }
}
