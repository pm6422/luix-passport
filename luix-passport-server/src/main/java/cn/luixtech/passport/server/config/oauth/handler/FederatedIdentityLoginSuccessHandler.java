package cn.luixtech.passport.server.config.oauth.handler;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.client.authentication.OAuth2AuthenticationToken;
import org.springframework.security.oauth2.core.oidc.user.OidcUser;
import org.springframework.security.oauth2.core.user.OAuth2User;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.security.web.authentication.SavedRequestAwareAuthenticationSuccessHandler;

import java.io.IOException;
import java.util.function.BiConsumer;

/**
 * An {@link AuthenticationSuccessHandler} for capturing the {@link OidcUser} or
 * {@link OAuth2User} for Federated Account Linking or JIT Account Provisioning.
 */
public final class FederatedIdentityLoginSuccessHandler implements AuthenticationSuccessHandler {

    private final AuthenticationSuccessHandler           defaultSuccessHandler = new SavedRequestAwareAuthenticationSuccessHandler();
    private       BiConsumer<OAuth2User, Authentication> oauth2UserHandler;
    private final BiConsumer<OidcUser, Authentication>   oidcUserHandler       = (user, authentication) -> this.oauth2UserHandler.accept(user, authentication);

    public FederatedIdentityLoginSuccessHandler(BiConsumer<OAuth2User, Authentication> oauth2UserHandler) {
        this.oauth2UserHandler = oauth2UserHandler;
    }

    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {
        if (authentication instanceof OAuth2AuthenticationToken) {
            if (authentication.getPrincipal() instanceof OidcUser) {
                this.oidcUserHandler.accept((OidcUser) authentication.getPrincipal(), authentication);
            } else if (authentication.getPrincipal() != null) {
                this.oauth2UserHandler.accept((OAuth2User) authentication.getPrincipal(), authentication);
            }
        }

        this.defaultSuccessHandler.onAuthenticationSuccess(request, response, authentication);
    }
}
