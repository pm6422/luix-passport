package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.UserNotification;
import cn.luixtech.passport.server.pojo.MyNotification;
import cn.luixtech.passport.server.repository.NotificationRepository;
import cn.luixtech.passport.server.service.UserNotificationService;
import cn.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
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
    private final NotificationRepository  notificationRepository;
    private final UserNotificationService userNotificationService;

    @Operation(summary = "find notifications for current user")
    @GetMapping("/api/user-notifications")
    public ResponseEntity<List<MyNotification>> getMyNotifications() {
        List<MyNotification> myNotifications = new ArrayList<>();
        List<UserNotification> userNotifications = userNotificationService.getUserNotifications(AuthUtils.getCurrentUserId());

        for (UserNotification userNotification : userNotifications) {
            notificationRepository.findById(userNotification.getNotificationId()).ifPresent(notification -> {
                myNotifications.add(MyNotification.of(userNotification, notification));
            });
        }
        return ResponseEntity.ok(myNotifications);
    }

    @Operation(summary = "mark user notification as read")
    @PutMapping("/api/user-notifications/mark-as-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        userNotificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "count unread notifications for current user")
    @GetMapping("/api/user-notifications/unread-count")
    public ResponseEntity<Long> getMyUnreadCount() {
        long count = userNotificationService.getUnreadCount(AuthUtils.getCurrentUserId());
        return ResponseEntity.ok(count);
    }
}
