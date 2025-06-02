package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.UserNotification;
import cn.luixtech.passport.server.pojo.MyNotification;
import cn.luixtech.passport.server.service.UserNotificationService;
import cn.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.ArrayList;
import java.util.List;

import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing user notifications.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class UserNotificationController {
    private final UserNotificationService userNotificationService;

    @Operation(summary = "find notifications for current user")
    @GetMapping("/api/user-notifications")
    public ResponseEntity<List<MyNotification>> getMyNotifications(@ParameterObject Pageable pageable,
                                                                   @RequestParam(value = "keyword", required = false) String keyword) {
        List<MyNotification> myNotifications = new ArrayList<>();
        Page<UserNotification> userNotifications = userNotificationService.findUserNotifications(pageable, AuthUtils.getCurrentUsername(), keyword);
        for (UserNotification userNotification : userNotifications) {
            myNotifications.add(MyNotification.of(userNotification));
        }
        return ResponseEntity.ok().headers(generatePageHeaders(userNotifications)).body(myNotifications);
    }

    @Operation(summary = "mark user notification as read")
    @PutMapping("/api/user-notifications/mark-as-read/{id}")
    public ResponseEntity<Void> markAsRead(@PathVariable String id) {
        userNotificationService.markAsRead(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "count unread notifications for current user")
    @GetMapping("/api/user-notifications/unread-count")
    public ResponseEntity<Long> countMyUnread() {
        long count = userNotificationService.countUnreadNotifications(AuthUtils.getCurrentUsername());
        return ResponseEntity.ok(count);
    }
}
