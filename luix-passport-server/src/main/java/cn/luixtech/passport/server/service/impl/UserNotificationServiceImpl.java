package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.UserNotification;
import cn.luixtech.passport.server.service.UserNotificationService;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class UserNotificationServiceImpl implements UserNotificationService {
    @Override
    public void markAsRead(Long userNotificationId) {

    }

    @Override
    public List<UserNotification> getUserNotifications(String userId) {
        return List.of();
    }
}
