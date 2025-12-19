package cn.luixtech.passport.server.event;

import cn.luixtech.passport.server.config.oauth.AuthUser;
import cn.luixtech.passport.server.service.LoginLogService;
import cn.luixtech.passport.server.service.UserService;
import com.luixtech.springbootframework.component.SseNotifier;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.util.List;

import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationEventListener {
    private final UserService userService;
    private final SessionRegistry sessionRegistry;
    private final SseNotifier sseNotifier;
    private final LoginLogService loginLogService;
    private final HttpServletRequest httpServletRequest;

    @EventListener
    public void authenticationSuccessEvent(AuthenticationSuccessEvent event) {
        if (event.getAuthentication().getPrincipal() instanceof AuthUser authUser) {
            userService.updateLastLoginTime(authUser.getUsername());
            loginLogService.recordLogin(authUser.getUsername(), "success", httpServletRequest);
            log.info("Authenticated successfully for user: {}", authUser.getUsername());
        }
    }

    @EventListener
    public void authenticationFailureEvent(AbstractAuthenticationFailureEvent event) {
        String username = cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername(event.getAuthentication());
        if (StringUtils.isNotEmpty(username)) {
            loginLogService.recordLogin(username, "failed", httpServletRequest);
            log.warn("Authenticate failure for user: {} with exception: {}", username,
                    event.getException().getMessage());
        }
    }

    @EventListener
    public void logoutEvent(LogoutEvent event) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            if (principal instanceof AuthUser authUser) {
                if (authUser.getUsername().equals(event.getUsername())) {
                    // remove user from sse
                    sseNotifier.removeUser(authUser.getUsername());

                    List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);
                    if (sessionsInfo != null && !sessionsInfo.isEmpty()) {
                        for (SessionInformation sessionInformation : sessionsInfo) {
                            log.info("Expire now :" + sessionInformation.getSessionId());
                            // Expire or logout the user
                            sessionInformation.expireNow();
                        }
                    }
                }
            }
        }
    }

    @EventListener
    public void logoutSuccessEvent(LogoutSuccessEvent event) {
        String username = getCurrentUsername();
        if (StringUtils.isNotEmpty(username)) {
            log.info("Logged out for user: [{}] and initiated by {}", username,
                    event.getSource().getClass().getSimpleName());
        }
    }

    @EventListener
    public void authorizationGrantedEvent(AuthorizationGrantedEvent<?> event) {
        log.info("Granted authorization for user: [{}] and initiated by {}",
                getCurrentUsername(event.getAuthentication().get()),
                event.getSource().getClass().getSimpleName());
    }

    @EventListener
    public void authorizationDeniedEvent(AuthorizationDeniedEvent<?> event) {
        log.warn("Denied authorization for user: [{}] and initiated by {}",
                getCurrentUsername(event.getAuthentication().get()),
                event.getSource().getClass().getSimpleName());
    }

    @EventListener
    public void authorizationEvent(AuthorizationEvent event) {
        String result = (event instanceof AuthorizationGrantedEvent) ? "GRANTED" : "DENIED";
        log.info("Authorization result: {} for user: [{}] and initiated by {}", result,
                getCurrentUsername(event.getAuthentication().get()), event.getSource().getClass().getSimpleName());
    }
}
