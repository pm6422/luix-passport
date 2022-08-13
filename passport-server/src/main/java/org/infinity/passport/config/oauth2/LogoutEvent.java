package org.infinity.passport.config.oauth2;

import org.springframework.context.ApplicationEvent;

public class LogoutEvent extends ApplicationEvent {
    private static final long serialVersionUID = -8253155525761915591L;

    /**
     * Create a new {@code ApplicationEvent}.
     *
     * @param source the object on which the event initially occurred or with
     *               which the event is associated (never {@code null})
     */
    public LogoutEvent(Object source) {
        super(source);
    }
}
