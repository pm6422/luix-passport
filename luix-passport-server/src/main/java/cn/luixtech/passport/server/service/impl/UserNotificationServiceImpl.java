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
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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
        notification.setType(type);
        notification.setSenderId(senderId);
        notification.setCreatedAt(Instant.now());
        notification.setModifiedAt(notification.getCreatedAt());
        notification = notificationRepository.save(notification);
        return notification;
    }

    private void sendNotification(User user, Notification notification) {
        UserNotification userNotification = new UserNotification();
        userNotification.setId(IdGenerator.generateId());
        userNotification.setReceiverId(user.getId());
        userNotification.setNotification(notification);
        userNotification.setStatus(UserNotification.STATUS_UNREAD);
        userNotification.setActive(true);
        userNotification.setCreatedAt(Instant.now());
        userNotification.setModifiedAt(userNotification.getCreatedAt());

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
        return userNotificationRepository.countByReceiverIdAndStatus(userId, UserNotification.STATUS_UNREAD);
    }

    @Override
    public void markAsRead(String userNotificationId) {
        userNotificationRepository.findById(userNotificationId).ifPresent(un -> {
            un.setStatus(UserNotification.STATUS_READ);
            userNotificationRepository.save(un);
        });
    }

    @Override
    public Page<UserNotification> getUserNotifications(Pageable pageable, String receiverId, String keyword) {
        return StringUtils.isEmpty(keyword)
                ? userNotificationRepository.findByReceiverId(pageable, receiverId)
                : userNotificationRepository.findByReceiverAndKeyword(pageable, receiverId, keyword);
    }
}
