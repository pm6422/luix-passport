package com.luixtech.passport.service.impl;

import com.google.common.collect.ImmutableMap;
import com.luixtech.passport.component.MessageCreator;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.dto.UsernameAndPasswordDTO;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.exception.DuplicationException;
import com.luixtech.passport.repository.UserAuthorityRepository;
import com.luixtech.passport.repository.UserRepository;
import com.luixtech.passport.service.UserService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;
import org.thymeleaf.util.StringUtils;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.concurrent.TimeUnit;

@Service
@AllArgsConstructor
@Slf4j
public class UserServiceImpl implements UserService {
    private final UserRepository          userRepository;
    private final UserAuthorityRepository userAuthorityRepository;
    private final PasswordEncoder         passwordEncoder;
    private final MessageCreator          messageCreator;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void changePassword(UsernameAndPasswordDTO dto) {
        userRepository.findOneByUsername(dto.getUsername()).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(dto.getNewPassword()));
            userRepository.save(user);
            log.debug("Changed password for user: {}", user);
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public User insert(User domain, String rawPassword) {
        Optional<User> existingUser = userRepository.findOneByUsername(domain.getUsername().toLowerCase(Locale.ENGLISH));
        if (existingUser.isPresent()) {
            throw new DuplicationException(ImmutableMap.of("username", domain.getUsername()));
        }
        if (findOneByEmail(domain.getEmail()).isPresent()) {
            throw new DuplicationException(ImmutableMap.of("email", domain.getEmail()));
        }
        if (findOneByMobileNo(domain.getMobileNo()).isPresent()) {
            throw new DuplicationException(ImmutableMap.of("mobileNo", domain.getMobileNo()));
        }

        domain.setUsername(domain.getUsername().toLowerCase());
        domain.setEmail(domain.getEmail().toLowerCase());
        domain.setPasswordHash(passwordEncoder.encode(rawPassword));
        domain.setActivationKey(RandomStringUtils.randomNumeric(20));
        domain.setResetKey(RandomStringUtils.randomNumeric(20));
        domain.setResetTime(Instant.now());
        userRepository.save(domain);
        log.debug("Created information for user: {}", domain);
        return domain;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void update(User domain) {
        userRepository.findById(domain.getId()).map(u -> {
            Optional<User> existingUser = findOneByEmail(domain.getEmail());
            if (existingUser.isPresent() && (!existingUser.get().getId().equalsIgnoreCase(domain.getId()))) {
                throw new DuplicationException(ImmutableMap.of("email", domain.getEmail()));
            }
            existingUser = findOneByMobileNo(domain.getMobileNo());
            if (existingUser.isPresent() && (!existingUser.get().getId().equalsIgnoreCase(domain.getId()))) {
                throw new DuplicationException(ImmutableMap.of("mobileNo", domain.getMobileNo()));
            }
            if (existingUser.isPresent() && !Boolean.TRUE.equals(domain.getActivated()) && Boolean.TRUE.equals(existingUser.get().getActivated())) {
                throw new IllegalArgumentException(messageCreator.getMessage("EP5021"));
            }

            u.setFirstName(domain.getFirstName());
            u.setLastName(domain.getLastName());
            u.setEmail(domain.getEmail().toLowerCase());
            u.setMobileNo(domain.getMobileNo());
            u.setEnabled(domain.getEnabled());
            u.setRemarks(domain.getRemarks());
            u.setAuthorities(domain.getAuthorities());
            userRepository.save(u);
            log.debug("Updated user: {}", domain);

            return u;
        }).orElseThrow(() -> new DataNotFoundException(domain.getId()));
    }

    @Override
    public User findOneByUsername(String username) {
        Assert.hasText(username, "it must not be null, empty, or blank");
        return userRepository.findOneByUsername(username.toLowerCase(Locale.ENGLISH))
                .orElseThrow(() -> new DataNotFoundException(username));
    }

    @Override
    public Optional<User> findOneByEmail(String email) {
        Assert.hasText(email, "it must not be null, empty, or blank");
        return userRepository.findOneByEmail(email.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Optional<User> findOneByMobileNo(String mobileNo) {
        Assert.hasText(mobileNo, "it must not be null, empty, or blank");
        return userRepository.findOneByMobileNo(mobileNo.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Optional<User> findOneByLogin(String login) {
        Assert.hasText(login, "it must not be null, empty, or blank");
        return userRepository.findOneByUsernameOrEmailOrMobileNo(login.toLowerCase(Locale.ENGLISH),
                login.toLowerCase(Locale.ENGLISH), login.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Page<User> findByLogin(Pageable pageable, String login) {
        if (StringUtils.isEmpty(login)) {
            return userRepository.findAll(pageable);
        }
        return userRepository.findByUsernameOrEmailOrMobileNo(pageable, login, login, login);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public Optional<User> activateRegistration(String activationKey) {
        return userRepository.findOneByActivationKey(activationKey).map(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            userRepository.save(user);
            log.debug("Activated user: {}", user);
            return user;
        });
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public User requestPasswordReset(String email, String resetKey) {
        User user = userRepository.findOneByEmailAndActivatedIsTrue(email)
                .orElseThrow(() -> new DataNotFoundException(messageCreator.getMessage("email")));
        user.setResetKey(RandomStringUtils.randomNumeric(20));
        user.setResetTime(Instant.now());
        userRepository.save(user);
        log.debug("Requested reset user password for reset key {}", resetKey);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public User completePasswordReset(String newRawPassword, String resetKey) {
        User user = userRepository.findOneByResetKey(resetKey)
                .filter(u -> u.getResetTime().isAfter(Instant.now().minusSeconds(TimeUnit.DAYS.toSeconds(1))))
                .orElseThrow(() -> new DataNotFoundException(messageCreator.getMessage("resetKey")));
        user.setPasswordHash(passwordEncoder.encode(newRawPassword));
        user.setResetKey(null);
        user.setResetTime(null);
        userRepository.save(user);
        log.debug("Reset user password for reset key {}", resetKey);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void deleteByUsername(String username) {
        User user = findOneByUsername(username);
        userRepository.deleteById(user.getId());
        userAuthorityRepository.deleteByUserId(user.getId());
    }
}