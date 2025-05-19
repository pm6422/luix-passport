package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Notification;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserNotification;
import cn.luixtech.passport.server.repository.NotificationRepository;
import cn.luixtech.passport.server.repository.UserNotificationRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserNotificationService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.List;

@Service
@AllArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {
    private final NotificationRepository     notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository             userRepository;

    @Override
    public void sendBroadcastNotification(String title, String content) {
        Notification notification = saveNotification(null, title, content, Notification.TYPE_SYSTEM);

        // Create user notifications for each user
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            sendNotification(user, notification);
        }
    }

    @Override
    public void sendPersonalNotification(String senderId, List<String> receiverIds, String title, String content) {
        Notification notification = saveNotification(senderId, title, content, Notification.TYPE_PERSONAL);

        // Create user notifications for each receiver
        List<User> receivers = userRepository.findAllById(receiverIds);
        for (User receiver : receivers) {
            sendNotification(receiver, notification);
        }
    }

    private Notification saveNotification(String senderId, String title, String content, String type) {
        Notification notification = new Notification();
        notification.setId(IdGenerator.generateId());
        notification.setTitle(title);
        notification.setContent(content);
        notification.setCreatedAt(Instant.now());
        notification.setType(type);
        notification.setSenderId(senderId);
        notification = notificationRepository.save(notification);
        return notification;
    }

    private void sendNotification(User user, Notification notification) {
        UserNotification userNotification = new UserNotification();
        userNotification.setUserId(user.getId());
        userNotification.setNotificationId(notification.getId());
        userNotification.setStatus(UserNotification.STATUS_UNREAD);
        userNotification.setActive(true);
        userNotificationRepository.save(userNotification);

        // 实时推送
//            messagingTemplate.convertAndSendToUser(
//                    user.getUsername(),
//                    "/queue/notifications",
//                    new NotificationDTO(notification.getId(), notification.getTitle())
//            );
    }

    @Override
    public long getUnreadCount(String userId) {
        return 0;
    }

    @Override
    public void markAsRead(Long userNotificationId) {

    }

    @Override
    public List<UserNotification> getUserNotifications(String userId) {
        return List.of();
    }
}
