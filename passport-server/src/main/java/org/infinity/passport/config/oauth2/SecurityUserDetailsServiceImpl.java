package org.infinity.passport.config.oauth2;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.User;
import org.infinity.passport.exception.UserNotActivatedException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.service.UserService;
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
public class SecurityUserDetailsServiceImpl implements org.springframework.security.core.userdetails.UserDetailsService {
    private final UserService             userService;
    private final UserAuthorityRepository userAuthorityRepository;

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

        return new SecurityUser(userFromDatabase.getId(), userFromDatabase.getUserName(),
                userFromDatabase.getPasswordHash(), userFromDatabase.getEnabled(),
                true, true, true, grantedAuthorities);
    }
}
