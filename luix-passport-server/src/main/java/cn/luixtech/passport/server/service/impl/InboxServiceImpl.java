package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Inbox;
import cn.luixtech.passport.server.domain.Notification;
import cn.luixtech.passport.server.repository.InboxRepository;
import cn.luixtech.passport.server.repository.NotificationRepository;
import cn.luixtech.passport.server.service.InboxService;
import com.github.f4b6a3.tsid.TsidCreator;
import com.luixtech.springbootframework.component.SseNotifier;
import com.luixtech.springbootframework.component.SseNotifier.SseMessage;
import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@AllArgsConstructor
public class InboxServiceImpl implements InboxService {
    private final InboxRepository        inboxRepository;
    private final NotificationRepository notificationRepository;
    private final SseNotifier            sseNotifier;

    @Override
    public void pushNotification(List<String> usernames, String title, String content) {
        pushNotification(usernames, title, content, null, null);
    }

    @Override
    public void pushNotification(List<String> usernames, String title, String content, String sender,
                                 String senderEmail) {
        Notification notification = saveNotification(title, content, sender, senderEmail);

        // Push notifications for each receiver
        for (String username : usernames) {
            pushNotification(username, notification);
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

    private Notification saveNotification(String title, String content, String sender, String senderEmail) {
        Notification notification = new Notification();
        notification.setTitle(title);
        notification.setContent(content);
        notification.setSender(sender);
        notification.setSenderEmail(senderEmail);
        return notificationRepository.save(notification);
    }

    private void pushNotification(String username, Notification notification) {
        Inbox inbox = new Inbox();
        inbox.setReceiverId(username);
        inbox.setNotification(notification);
        inbox.setStatus(Inbox.STATUS_UNREAD);
        inboxRepository.save(inbox);

        SseMessage msg = new SseMessage();
        msg.setId(StringUtils.EMPTY + TsidCreator.getTsid().toLong());
        msg.setName(notification.getTitle());
        msg.setData(notification);
        sseNotifier.send(username, msg);
    }

    @Override
    public Page<Inbox> findInboxes(Pageable pageable, String receiverId, String keyword) {
        return StringUtils.isEmpty(keyword)
                ? inboxRepository.findByReceiverId(pageable, receiverId)
                : inboxRepository.findByReceiverAndKeyword(pageable, receiverId, keyword);
    }
}
