package cn.luixtech.passport.server.dto;

import cn.luixtech.passport.server.domain.Inbox;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@NoArgsConstructor
public class InboxDto {
    private String  id;
    private String  notificationId;
    private String  title;
    private String  content;
    private String  type;
    private String  sender;
    private String  senderEmail;
    private String  receiverId;
    private String  status;
    private Boolean active;
    private Instant createdAt;
    private Instant modifiedAt;

    public static InboxDto of(Inbox inbox) {
        InboxDto resp = new InboxDto();
        BeanUtils.copyProperties(inbox.getNotification(), resp);
        BeanUtils.copyProperties(inbox, resp);
        resp.setNotificationId(inbox.getNotification().getId());
        return resp;
    }
}
