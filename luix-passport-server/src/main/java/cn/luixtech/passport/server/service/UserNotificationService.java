package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.UserNotification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface UserNotificationService {
    void sendBroadcastNotification(String title, String content);

    void sendPersonalNotification(String senderId, List<String> receiverIds, String title, String content);

    long getUnreadCount(String userId);

    void markAsRead(String userNotificationId);

    Page<UserNotification> findserNotifications(Pageable pageable, String receiverId, String keyword);

}
