package com.luixtech.passport.config.oauth2.service;

import com.luixtech.passport.config.oauth2.SecurityUser;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.exception.UserNotActivatedException;
import com.luixtech.passport.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@AllArgsConstructor
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService userService;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        if (StringUtils.isEmpty(login)) {
            log.warn("login must not be empty!");
            throw new BadCredentialsException("login must not be empty");
        }
        User user = userService.findOneByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found"));
        if (!Boolean.TRUE.equals(user.getActivated())) {
            throw new UserNotActivatedException(login);
        }
        List<GrantedAuthority> grantedAuthorities = user.getAuthorities()
                .stream()
                .map(userAuthority -> new SimpleGrantedAuthority(userAuthority.getAuthorityName()))
                .collect(Collectors.toList());

        return new SecurityUser(user.getId(), user.getUsername(),
                user.getFirstName(), user.getLastName(),
                user.getPasswordHash(), user.getEnabled(),
                true, true,
                true, grantedAuthorities);
    }
}
