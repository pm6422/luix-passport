package org.infinity.passport.security;

import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security logout handler, specialized for Ajax requests.
 */
@Component
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler
        implements LogoutSuccessHandler {

    private final TokenStore tokenStore;

    public AjaxLogoutSuccessHandler(TokenStore tokenStore) {
        this.tokenStore = tokenStore;
    }

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Remove the access token
        String token = request.getHeader("authorization");
        if (token != null && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            final OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(StringUtils
                    .substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim());

            if (oAuth2AccessToken != null) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
            }
        }

        response.setStatus(HttpServletResponse.SC_OK);
    }
}
