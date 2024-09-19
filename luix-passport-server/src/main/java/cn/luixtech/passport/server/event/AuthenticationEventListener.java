package cn.luixtech.passport.server.event;

import cn.luixtech.passport.server.config.oauth.AuthUser;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserAuthEvent;
import cn.luixtech.passport.server.repository.UserAuthEventRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import com.luixtech.utilities.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.context.event.EventListener;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.authentication.event.AbstractAuthenticationFailureEvent;
import org.springframework.security.authentication.event.AuthenticationSuccessEvent;
import org.springframework.security.authentication.event.LogoutSuccessEvent;
import org.springframework.security.authorization.event.AuthorizationDeniedEvent;
import org.springframework.security.authorization.event.AuthorizationEvent;
import org.springframework.security.authorization.event.AuthorizationGrantedEvent;
import org.springframework.security.core.session.SessionInformation;
import org.springframework.security.core.session.SessionRegistry;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.util.List;

import static cn.luixtech.passport.server.domain.UserAuthEvent.AUTH_FAILURE;
import static cn.luixtech.passport.server.domain.UserAuthEvent.AUTH_SUCCESS;
import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUserId;
import static cn.luixtech.passport.server.utils.AuthUtils.getCurrentUsername;

@Slf4j
@Component
@AllArgsConstructor
public class AuthenticationEventListener {
    private       UserAuthEventRepository userAuthEventRepository;
    private final UserRepository          userRepository;
    private       UserService             userService;
    private       SessionRegistry         sessionRegistry;

    @EventListener
    public void authenticationSuccessEvent(AuthenticationSuccessEvent event) {
        if (event.getSource() instanceof UsernamePasswordAuthenticationToken token) {
            if (token.getPrincipal() instanceof AuthUser authUser) {
                UserAuthEvent domain = new UserAuthEvent();
                domain.setUserId(authUser.getId());
                domain.setEvent(AUTH_SUCCESS);
                domain.setRemark(event.getSource().getClass().getSimpleName());
                userAuthEventRepository.save(domain);

                User user = userRepository.findById(authUser.getId()).orElseThrow(() -> new DataNotFoundException(authUser.getId()));
                user.setLastSignInAt(LocalDateTime.now());
                userService.update(user);
                log.info("Authenticated successfully for user: {}", authUser.getId());
            }
        }
    }

    @EventListener
    public void authenticationFailureEvent(AbstractAuthenticationFailureEvent event) {
        String userId = getCurrentUserId();
        if (StringUtils.isNotEmpty(userId)) {
            // insert
            UserAuthEvent domain = new UserAuthEvent();
            domain.setUserId(userId);
            domain.setEvent(AUTH_FAILURE);
            domain.setRemark(StringUtils.abbreviate(event.getException().getMessage(), 64));
            userAuthEventRepository.save(domain);
            log.warn("Authenticate failure for user: {} with exception: {}", userId, event.getException().getMessage());
        }
    }

    @EventListener
    public void logoutEvent(LogoutEvent event) {
        List<Object> allPrincipals = sessionRegistry.getAllPrincipals();
        for (Object principal : allPrincipals) {
            if (principal instanceof AuthUser authUser) {
                if (authUser.getUsername().equals(event.getUsername())) {
                    List<SessionInformation> sessionsInfo = sessionRegistry.getAllSessions(principal, false);
                    if (null != sessionsInfo && !sessionsInfo.isEmpty()) {
                        for (SessionInformation sessionInformation : sessionsInfo) {
                            log.info("Expire now :" + sessionInformation.getSessionId());
                            //Expire or logout the user
                            sessionInformation.expireNow();
                        }
                    }
                }
            }
        }
    }


    @EventListener
    public void logoutSuccessEvent(LogoutSuccessEvent event) {
        String userId = getCurrentUserId();
        if (StringUtils.isNotEmpty(userId)) {
            // insert
            UserAuthEvent domain = new UserAuthEvent();
            domain.setId(IdGenerator.generateId());
            domain.setUserId(userId);
            domain.setEvent("LogoutSuccess");
            domain.setRemark(event.getSource().getClass().getSimpleName());
            userAuthEventRepository.save(domain);
            log.info("Logged out for user: [{}] and initiated by {}", userId, event.getSource().getClass().getSimpleName());
        }
    }

    @EventListener
    public void authorizationGrantedEvent(AuthorizationGrantedEvent<?> event) {
        log.info("Granted authorization for user: [{}] and initiated by {}", getCurrentUsername(event.getAuthentication().get()),
                event.getSource().getClass().getSimpleName());
    }

    @EventListener
    public void authorizationDeniedEvent(AuthorizationDeniedEvent<?> event) {
        log.warn("Denied authorization for user: [{}] and initiated by {}", getCurrentUsername(event.getAuthentication().get()),
                event.getSource().getClass().getSimpleName());
    }

    @EventListener
    public void authorizationEvent(AuthorizationEvent event) {
        log.info("Authorization result: {} for user: [{}] and initiated by {}", event.getAuthorizationDecision(),
                getCurrentUsername(event.getAuthentication().get())
                , event.getSource().getClass().getSimpleName());
    }
}