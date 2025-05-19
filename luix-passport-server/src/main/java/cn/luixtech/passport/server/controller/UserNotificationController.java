package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.UserNotification;
import cn.luixtech.passport.server.service.UserNotificationService;
import cn.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_USER;

/**
 * REST controller for managing user notifications.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_USER + "\")")
@Slf4j
public class UserNotificationController {
    private final UserNotificationService userNotificationService;

    @Operation(summary = "find user notifications")
    @GetMapping("/api/user-notifications")
    public ResponseEntity<List<UserNotification>> getMyNotifications() {
        List<UserNotification> userNotifications = userNotificationService.getUserNotifications(AuthUtils.getCurrentUserId());
        return ResponseEntity.ok(userNotifications);
    }

    @Operation(summary = "mark user notification as read")
    @PostMapping("/api/user-notifications/mark-as-read/{userNotificationId}")
    public ResponseEntity<Void> markAsRead(@PathVariable String userNotificationId) {
        userNotificationService.markAsRead(userNotificationId);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/unread-count")
    public ResponseEntity<Long> getMyUnreadCount() {
        long count = userNotificationService.getUnreadCount(AuthUtils.getCurrentUserId());
        return ResponseEntity.ok(count);
    }
}
