package org.infinity.passport.config.oauth2;

import lombok.AllArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.security.web.authentication.AbstractAuthenticationTargetUrlRequestHandler;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

/**
 * Spring Security logout handler, specialized for Ajax requests.
 */
@AllArgsConstructor
public class AjaxLogoutSuccessHandler extends AbstractAuthenticationTargetUrlRequestHandler implements LogoutSuccessHandler {
    private final TokenStore tokenStore;

    @Override
    public void onLogoutSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) {
        // Remove the access token
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            String accessToken = StringUtils.substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim();
            OAuth2AccessToken oAuth2AccessToken = tokenStore.readAccessToken(accessToken);
            if (oAuth2AccessToken != null) {
                tokenStore.removeAccessToken(oAuth2AccessToken);
            }
        }
        response.setStatus(HttpServletResponse.SC_OK);
    }
}
