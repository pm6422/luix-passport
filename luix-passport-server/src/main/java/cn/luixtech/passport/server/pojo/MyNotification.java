package cn.luixtech.passport.server.pojo;

import cn.luixtech.passport.server.domain.UserNotification;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;

@Data
@NoArgsConstructor
public class MyNotification {
    private String  id;
    private String  notificationId;
    private String  title;
    private String  content;
    private String  type;
    private String  senderId;
    private String  receiverId;
    private String  status;
    private Boolean active;
    private Instant createdAt;
    private Instant modifiedAt;

    public static MyNotification of(UserNotification userNotification) {
        MyNotification resp = new MyNotification();
        BeanUtils.copyProperties(userNotification.getNotification(), resp);
        BeanUtils.copyProperties(userNotification, resp);
        resp.setNotificationId(userNotification.getNotification().getId());
        return resp;
    }
}
