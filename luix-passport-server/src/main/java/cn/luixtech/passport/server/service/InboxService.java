package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.Inbox;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface InboxService {

    void pushNotification(List<String> usernames, String title, String content);

    void pushNotification(List<String> usernames, String title, String content, String sender, String senderEmail);

    long countUnread(String username);

    void markAsRead(String inboxId);

    Page<Inbox> findInboxes(Pageable pageable, String receiverId, String keyword);

}
