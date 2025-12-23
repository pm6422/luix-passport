package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.service.UserAvatarService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static cn.luixtech.passport.server.domain.Role.ROLE_ADMIN;

/**
 * REST controller for managing users' avatar picture.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class UserAvatarController {
    private final UserAvatarService userAvatarService;

    @Operation(summary = "find user avatar by username")
    @GetMapping("/api/user-avatars/{username}")
    public ResponseEntity<byte[]> findById(HttpServletRequest request,
            @Parameter(description = "username", required = true) @PathVariable String username) throws IOException {
        return ResponseEntity.ok(userAvatarService.getAvatar(username, request));
    }
}
