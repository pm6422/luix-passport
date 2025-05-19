package cn.luixtech.passport.server.pojo;

import cn.luixtech.passport.server.domain.Notification;
import cn.luixtech.passport.server.domain.UserNotification;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

@Data
@NoArgsConstructor
public class UserNotificationResp {
    private String  id;
    private String  notificationId;
    private String  title;
    private String  content;
    private String  type;
    private String  senderId;
    private String  receiverId;
    private String  status;
    private Boolean active;

    public static UserNotificationResp of(UserNotification userNotification, Notification notification) {
        UserNotificationResp resp = new UserNotificationResp();
        BeanUtils.copyProperties(notification, resp);
        BeanUtils.copyProperties(userNotification, resp);
        resp.setNotificationId(notification.getId());
        resp.setReceiverId(userNotification.getUserId());
        return resp;
    }
}
