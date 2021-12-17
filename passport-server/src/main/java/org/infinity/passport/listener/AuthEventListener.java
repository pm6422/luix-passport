package org.infinity.passport.listener;

import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.event.LogoutEvent;
import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

@Component
@Slf4j
public class AuthEventListener {

    @Resource
    private AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler;

    @Async
    @EventListener
    public void logoutEvent(LogoutEvent event) {
        log.debug("Processing logout event initiated by {}", event.getSource().getClass().getSimpleName());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes != null ? servletRequestAttributes.getRequest() : null;
        HttpServletResponse response = servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
        if (request != null && response != null) {
            ajaxLogoutSuccessHandler.onLogoutSuccess(request, response, null);
        }
        log.debug("Processed logout event");
    }
}
