package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.UserNotification;

import java.util.List;

public interface UserNotificationService {
    void sendBroadcastNotification(String title, String content);

    void sendPersonalNotification(String senderId, List<String> receiverIds, String title, String content);

    long getUnreadCount(String userId);

    void markAsRead(Long userNotificationId);

    List<UserNotification> getUserNotifications(String userId);

}
