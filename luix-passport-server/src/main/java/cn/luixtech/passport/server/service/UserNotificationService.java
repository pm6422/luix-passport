package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserNotificationService {
    void sendBroadcastNotification(String title, String content, String sender, String senderEmail);

    void sendPersonalNotification(List<String> receiverIds, String title, String content);

    void sendPersonalNotification(List<String> receiverIds, String title, String content, String sender, String senderEmail);

    long countUnreadNotifications(String username);

    void markAsRead(String userNotificationId);

    Page<UserNotification> findUserNotifications(Pageable pageable, String receiverId, String keyword);

}
