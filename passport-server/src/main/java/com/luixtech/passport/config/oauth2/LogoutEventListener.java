package com.luixtech.passport.config.oauth2;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.event.EventListener;
import org.springframework.scheduling.annotation.Async;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Slf4j
@Component
@AllArgsConstructor
public class LogoutEventListener {
    private final LogoutSuccessHandler logoutSuccessHandler;

    @Async
    @EventListener
    public void logoutEvent(LogoutEvent event) throws ServletException, IOException {
        log.debug("Processing logout event initiated by {}", event.getSource().getClass().getSimpleName());
        ServletRequestAttributes servletRequestAttributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
        HttpServletRequest request = servletRequestAttributes != null ? servletRequestAttributes.getRequest() : null;
        HttpServletResponse response = servletRequestAttributes != null ? servletRequestAttributes.getResponse() : null;
        if (request != null && response != null) {
            logoutSuccessHandler.onLogoutSuccess(request, response, null);
        }
        log.debug("Processed logout event");
    }
}
