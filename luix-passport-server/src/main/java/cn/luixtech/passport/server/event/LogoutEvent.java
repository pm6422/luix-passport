package cn.luixtech.passport.server.event;

import lombok.Getter;
import org.springframework.context.ApplicationEvent;

@Getter
public class LogoutEvent extends ApplicationEvent {
    private static final long serialVersionUID = -8253155525761915591L;
    private String username;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public LogoutEvent(Object source, String username) {
        super(source);
        this.username = username;
    }
}
