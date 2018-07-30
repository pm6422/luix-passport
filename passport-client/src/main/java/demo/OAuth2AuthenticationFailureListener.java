package demo;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.ApplicationListener;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.oauth2.client.filter.OAuth2AuthenticationFailureEvent;
import org.springframework.stereotype.Component;

@Component
public class OAuth2AuthenticationFailureListener implements ApplicationListener<OAuth2AuthenticationFailureEvent> {

    @Autowired
    private HttpServletRequest request;

    @Override
    public void onApplicationEvent(OAuth2AuthenticationFailureEvent event) {
        if (event.getException() instanceof BadCredentialsException) {
            // Get current session
            HttpSession session = request.getSession(false);
            if (session != null) {
                session.invalidate();
            }
        }
    }
}