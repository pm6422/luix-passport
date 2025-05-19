package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.UserNotification;

import java.util.List;

public interface UserNotificationService {
    void markAsRead(Long userNotificationId);

    List<UserNotification> getUserNotifications(String userId);
}
