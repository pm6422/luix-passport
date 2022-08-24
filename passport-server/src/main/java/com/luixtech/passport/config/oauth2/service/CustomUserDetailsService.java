package com.luixtech.passport.config.oauth2.service;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import com.luixtech.passport.config.oauth2.SecurityUser;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.exception.UserNotActivatedException;
import com.luixtech.passport.repository.UserAuthorityRepository;
import com.luixtech.passport.service.UserService;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
@Slf4j
@AllArgsConstructor
public class CustomUserDetailsService implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserAuthorityRepository userAuthorityRepository;
    private final UserService             userService;

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
}
