package com.luixtech.passport.config.oauth2.service;

import com.luixtech.passport.config.oauth2.SecurityUser;
import com.luixtech.passport.config.oauth2.SecurityUtils;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@AllArgsConstructor
public class SecurityUserService {
    private final OAuth2AuthorizationService oAuth2AuthorizationService;

    public SecurityUser getUserByAccessToken(HttpServletRequest request) {
        String accessToken = SecurityUtils.getAccessToken(request);
        if (StringUtils.isNotEmpty(accessToken)) {
            OAuth2Authorization oAuth2Authorization = oAuth2AuthorizationService.findByToken(accessToken, OAuth2TokenType.ACCESS_TOKEN);
            if (oAuth2Authorization != null && oAuth2Authorization.getAttribute(Principal.class.getName()) != null) {
                Object attribute = oAuth2Authorization.getAttribute(Principal.class.getName());
                if (attribute instanceof Authentication) {
                    Authentication authentication = (Authentication) attribute;
                    Object principal = authentication.getPrincipal();
                    if (principal != null && principal instanceof SecurityUser) {
                        return (SecurityUser) principal;
                    }
                }
            }
        }
        return null;
    }
}
