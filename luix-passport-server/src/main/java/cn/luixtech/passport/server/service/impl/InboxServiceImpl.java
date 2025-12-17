package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Inbox;
import cn.luixtech.passport.server.domain.Notification;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.repository.InboxRepository;
import cn.luixtech.passport.server.repository.NotificationRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.InboxService;
import com.luixtech.springbootframework.component.SseNotifier;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InboxServiceImpl implements InboxService {
    private final NotificationRepository notificationRepository;
    private final InboxRepository        inboxRepository;
    private final UserRepository         userRepository;
    private final SseNotifier            sseNotifier;

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
    public void sendPersonalNotification(List<String> receiverIds, String title, String content, String sender,
                                         String senderEmail) {
        Notification notification = saveNotification(title, content, Notification.TYPE_PERSONAL, sender, senderEmail);

        // Create user notifications for each receiver
        List<User> receivers = userRepository.findAllById(receiverIds);
        for (User receiver : receivers) {
            sendNotification(receiver, notification);
        }
    }

    @Override
    public long countUnread(String username) {
        return inboxRepository.countByReceiverIdAndStatus(username, Inbox.STATUS_UNREAD);
    }

    @Override
    public void markAsRead(String inboxId) {
        inboxRepository.findById(inboxId).ifPresent(inbox -> {
            inbox.setStatus(Inbox.STATUS_READ);
            inboxRepository.save(inbox);
        });
    }

    private void sendNotification(User user, Notification notification) {
        Inbox inbox = new Inbox();
        inbox.setReceiverId(user.getUsername());
        inbox.setNotification(notification);
        inbox.setStatus(Inbox.STATUS_UNREAD);
        inbox.setActive(true);

        inboxRepository.save(inbox);

        sseNotifier.send(user.getUsername(), notification.getTitle());
    }

    private Notification saveNotification(String title, String content, String type, String sender, String senderEmail) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setType(type);
        notification.setSender(sender);
        notification.setSenderEmail(senderEmail);
        return notificationRepository.save(notification);
    }

    @Override
    public Page<Inbox> findInboxes(Pageable pageable, String receiverId, String keyword) {
        return StringUtils.isEmpty(keyword)
                ? inboxRepository.findByReceiverId(pageable, receiverId)
                : inboxRepository.findByReceiverAndKeyword(pageable, receiverId, keyword);
    }
}
