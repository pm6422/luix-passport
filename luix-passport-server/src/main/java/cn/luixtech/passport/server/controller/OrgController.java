package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.repository.OrgRepository;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;

/**
 * REST controller for managing organizations.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class OrgController {
    private final OrgRepository orgRepository;

    @Operation(summary = "count enabled organizations")
    @GetMapping("/api/orgs/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(orgRepository.count());
    }
}
