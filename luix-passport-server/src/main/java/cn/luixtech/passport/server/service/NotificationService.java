package cn.luixtech.passport.server.service;

import java.util.List;

public interface NotificationService {
    void sendBroadcastNotification(String title, String content);

    void sendPersonalNotification(String senderId, List<String> receiverIds, String title, String content);

    long getUnreadCount(String userId);


}
