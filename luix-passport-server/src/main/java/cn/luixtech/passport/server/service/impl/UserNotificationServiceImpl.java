package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Notification;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserNotification;
import cn.luixtech.passport.server.pojo.SseMessage;
import cn.luixtech.passport.server.queue.JobProducer;
import cn.luixtech.passport.server.repository.NotificationRepository;
import cn.luixtech.passport.server.repository.UserNotificationRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserNotificationService;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

import static cn.luixtech.passport.server.queue.consumer.SseConsumer.CHANNEL_SSE_BROADCAST;

@Service
@AllArgsConstructor
public class UserNotificationServiceImpl implements UserNotificationService {
    private final NotificationRepository     notificationRepository;
    private final UserNotificationRepository userNotificationRepository;
    private final UserRepository             userRepository;
    private final JobProducer                jobProducer;

    @Override
    public void sendBroadcastNotification(String title, String content, String sender, String senderEmail) {
        Notification notification = saveNotification(title, content, Notification.TYPE_SYSTEM, sender, senderEmail);

        // Create user notifications for each user
        List<User> allUsers = userRepository.findAll();
        for (User user : allUsers) {
            sendNotification(user, notification);
        }
    }

    @Override
    public void sendPersonalNotification(List<String> receiverIds, String title, String content) {
        sendPersonalNotification(receiverIds, title, content, null, null);
    }

    @Override
    public void sendPersonalNotification(List<String> receiverIds, String title, String content, String sender, String senderEmail) {
        Notification notification = saveNotification(title, content, Notification.TYPE_PERSONAL, sender, senderEmail);

        // Create user notifications for each receiver
        List<User> receivers = userRepository.findAllById(receiverIds);
        for (User receiver : receivers) {
            sendNotification(receiver, notification);
        }
    }

    private Notification saveNotification(String title, String content, String type, String sender, String senderEmail) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setSender(sender);
        notification.setSenderEmail(senderEmail);
        notification = notificationRepository.save(notification);

        return notification;
    }

    private void sendNotification(User user, Notification notification) {
        UserNotification userNotification = new UserNotification();
        userNotification.setReceiverId(user.getUsername());
        userNotification.setNotification(notification);
        userNotification.setStatus(UserNotification.STATUS_UNREAD);
        userNotification.setActive(true);

        userNotificationRepository.save(userNotification);

        jobProducer.enqueue(CHANNEL_SSE_BROADCAST, SseMessage.buildPush(user.getUsername(), notification.getTitle()), true);
    }

    @Override
    public long countUnreadNotifications(String username) {
        return userNotificationRepository.countByReceiverIdAndStatus(username, UserNotification.STATUS_UNREAD);
    }

    @Override
    public void markAsRead(String userNotificationId) {
        userNotificationRepository.findById(userNotificationId).ifPresent(un -> {
            un.setStatus(UserNotification.STATUS_READ);
            userNotificationRepository.save(un);
        });
    }

    @Override
    public Page<UserNotification> findUserNotifications(Pageable pageable, String receiverId, String keyword) {
        return StringUtils.isEmpty(keyword)
                ? userNotificationRepository.findByReceiverId(pageable, receiverId)
                : userNotificationRepository.findByReceiverAndKeyword(pageable, receiverId, keyword);
    }
}
