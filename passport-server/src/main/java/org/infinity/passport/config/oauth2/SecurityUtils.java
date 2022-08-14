package org.infinity.passport.config.oauth2;

import org.apache.commons.lang3.StringUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

import javax.servlet.http.HttpServletRequest;

/**
 * Utility class for Spring Security.
 */
public abstract class SecurityUtils {
    /**
     * Get the name of the current user.
     */
    public static String getCurrentUsername() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        String username = null;
        if (authentication != null) {
            if (authentication instanceof JwtAuthenticationToken) {
                return authentication.getName();
            } else if (authentication.getPrincipal() instanceof UserDetails) {
                UserDetails springSecurityUser = (UserDetails) authentication.getPrincipal();
                username = springSecurityUser.getUsername();
            } else if (authentication.getPrincipal() instanceof String) {
                username = (String) authentication.getPrincipal();
            }
        }
        return username;
    }

    /**
     * Get access token from {@link HttpServletRequest}.
     *
     * @param request the request to extract the token from
     * @return the token if found, or empty string if not found
     */
    public static String getAccessToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (StringUtils.isNotEmpty(token) && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            String accessToken = StringUtils.substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim();
            return accessToken;
        }
        return StringUtils.EMPTY;
    }
}
