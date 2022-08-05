package org.infinity.passport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.config.oauth2.SecurityUser;
import org.infinity.passport.domain.User;
import org.infinity.passport.exception.UserDisabledException;
import org.infinity.passport.exception.UserNotActivatedException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.service.UserService;
import org.springframework.context.annotation.Lazy;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Authenticate a user from the database.
 */
//@Service
@Slf4j
public class SpringSecurityUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {

    private final UserService             userService;
    private final UserAuthorityRepository userAuthorityRepository;

    // Use @Lazy to fix dependencies problems
    public SpringSecurityUserDetailsServiceImpl(@Lazy UserService userService,
                                                UserAuthorityRepository userAuthorityRepository) {
        this.userService = userService;
        this.userAuthorityRepository = userAuthorityRepository;
    }

    @Override
    // @Transactional
    public UserDetails loadUserByUsername(final String login) {
        log.debug("Authenticating {}", login);
        User userFromDatabase = userService.findOneByLogin(login)
                .orElseThrow(() -> new UsernameNotFoundException("User " + login + " was not found in the database"));

        if (!userFromDatabase.getActivated()) {
            throw new UserNotActivatedException("User " + login + " was not activated");
        }

        if (!Boolean.TRUE.equals(userFromDatabase.getEnabled())) {
            throw new UserDisabledException("User " + login + " was disabled");
        }

        List<GrantedAuthority> grantedAuthorities = userAuthorityRepository.findByUserId(userFromDatabase.getId())
                .stream().map(userAuthority -> new SimpleGrantedAuthority(userAuthority.getAuthorityName()))
                .collect(Collectors.toList());
        return new SecurityUser(userFromDatabase.getId(), userFromDatabase.getUserName(),
                userFromDatabase.getPasswordHash(), grantedAuthorities);
    }
}
