package org.infinity.passport.config.oauth2;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.User;
import org.infinity.passport.exception.UserNotActivatedException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.service.UserService;
import org.springframework.http.HttpHeaders;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.core.OAuth2TokenType;
import org.springframework.security.oauth2.server.authorization.OAuth2Authorization;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;

import javax.servlet.http.HttpServletRequest;
import java.security.Principal;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@AllArgsConstructor
public class SecurityUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserAuthorityRepository    userAuthorityRepository;
    private final UserService                userService;
    private       OAuth2AuthorizationService oAuth2AuthorizationService;

    @Override
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        if (StringUtils.isEmpty(login)) {
            log.warn("login must not be empty!");
            throw new BadCredentialsException("login must not be empty");
        }
        User userFromDatabase = userService.findOneByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found"));
        if (!Boolean.TRUE.equals(userFromDatabase.getActivated())) {
            throw new UserNotActivatedException(login);
        }
        List<GrantedAuthority> grantedAuthorities = userAuthorityRepository.findByUserId(userFromDatabase.getId())
                .stream().map(userAuthority -> new SimpleGrantedAuthority(userAuthority.getAuthorityName()))
                .collect(Collectors.toList());

        return new SecurityUser(userFromDatabase.getId(), userFromDatabase.getUsername(),
                userFromDatabase.getPasswordHash(), userFromDatabase.getEnabled(),
                true, true, true, grantedAuthorities);
    }

    public SecurityUser getUserByAccessToken(HttpServletRequest request) {
        String token = request.getHeader(HttpHeaders.AUTHORIZATION);
        if (token != null && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            String accessToken = StringUtils.substringAfter(token, OAuth2AccessToken.BEARER_TYPE).trim();
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
