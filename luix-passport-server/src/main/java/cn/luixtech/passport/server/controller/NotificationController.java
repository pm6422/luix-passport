package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.Notification;
import cn.luixtech.passport.server.service.UserNotificationService;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@Slf4j
@RestController
@AllArgsConstructor
public class NotificationController {
    private final HttpHeaderCreator       httpHeaderCreator;
    private final UserNotificationService userNotificationService;

    @Operation(summary = "create a new oauth2 registered client")
    @PostMapping("/open-api/notifications/contact")
    public ResponseEntity<Void> createContactUsNotification(@Parameter(description = "notification", required = true) @Valid @RequestBody Notification domain) {
        userNotificationService.sendPersonalNotification(List.of("louis", "admin"),
                domain.getTitle(), domain.getContent(), domain.getSender(), domain.getSenderEmail());
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getId()))
                .build();
    }
}
