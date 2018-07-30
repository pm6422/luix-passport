package org.infinity.passport.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.infinity.passport.domain.User;
import org.infinity.passport.exception.UserDisabledException;
import org.infinity.passport.exception.UserNotActivatedException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

/**
 * Authenticate a user from the database.
 */
@Service
public class SpringSecurityUserDetailsServiceImpl
        implements org.springframework.security.core.userdetails.UserDetailsService {

    private static final Logger     LOGGER = LoggerFactory.getLogger(SpringSecurityUserDetailsServiceImpl.class);

    @Autowired
    private UserService             userService;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Override
    // @Transactional
    public UserDetails loadUserByUsername(final String login) {
        LOGGER.debug("Authenticating {}", login);
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
        return new org.springframework.security.core.userdetails.User(userFromDatabase.getUserName(),
                userFromDatabase.getPasswordHash(), grantedAuthorities);
    }
}
