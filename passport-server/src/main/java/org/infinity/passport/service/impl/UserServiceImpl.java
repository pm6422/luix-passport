package org.infinity.passport.service.impl;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
public class UserServiceImpl implements UserService {

    private static final Logger     LOGGER = LoggerFactory.getLogger(UserServiceImpl.class);

    @Autowired
    private UserRepository          userRepository;

    @Autowired
    private UserAuthorityRepository userAuthorityRepository;

    @Autowired
    private PasswordEncoder         passwordEncoder;

    // private void removeUserToken(User user) {
    // String clientId =
    // applicationProperties.getSecurity().getAuthentication().getOauth().getClientid();
    // tokenStore.findTokensByClientIdAndUserName(clientId,
    // user.getUserName()).stream()
    // .forEach(token -> tokenStore.removeAccessToken(token));
    // }

    @Override
    public void changePassword(String userName, String newRawPassword) {
        Assert.hasText(userName, "it must not be null, empty, or blank");
        Assert.hasText(newRawPassword, "it must not be null, empty, or blank");
        userRepository.findOneByUserName(userName).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(newRawPassword));
            userRepository.save(user);
            LOGGER.debug("Changed password for User: {}", user);
        });
    }

    @Override
    public String getPasswordHash(String rawPassword) {
        Assert.hasText(rawPassword, "it must not be null, empty, or blank");
        return passwordEncoder.encode(rawPassword);
    }

    @Override
    public User insert(String userName, String rawPassword, String firstName, String lastName, String email,
            String mobileNo, String activationKey, Boolean activated, String avatarImageUrl, Boolean enabled,
            String resetKey, Instant resetTime, Set<String> authorityNames) {
        User newUser = new User();
        newUser.setUserName(userName.toLowerCase());
        newUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setMobileNo(mobileNo);
        newUser.setEmail(email.toLowerCase());
        newUser.setActivated(activated);
        newUser.setActivationKey(activationKey);
        newUser.setAvatarImageUrl(avatarImageUrl);
        newUser.setResetKey(resetKey);
        newUser.setResetTime(resetTime);
        newUser.setEnabled(enabled);
        // These four values are set by Auditing mechanism
        //        newUser.setCreatedBy(createdBy);
        //        newUser.setCreatedTime(Instant.now());
        //        newUser.setModifiedBy(createdBy);
        //        newUser.setModifiedTime(newUser.getCreatedTime());

        userRepository.save(newUser);

        if (CollectionUtils.isEmpty(authorityNames)) {
            userAuthorityRepository.insert(new UserAuthority(newUser.getId(), Authority.USER));
        } else {
            authorityNames.forEach(authorityName -> {
                userAuthorityRepository.insert(new UserAuthority(newUser.getId(), authorityName));
            });
        }

        LOGGER.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    @Override
    public void update(String userName, String firstName, String lastName, String email, String mobileNo,
            String modifiedBy, Boolean activated, String avatarImageUrl, Boolean enabled, Set<String> authorityNames) {
        userRepository.findOneByUserName(userName.toLowerCase()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email.toLowerCase());
            user.setMobileNo(mobileNo);
            user.setModifiedBy(modifiedBy);
            user.setModifiedTime(Instant.now());
            user.setActivated(activated);
            user.setAvatarImageUrl(avatarImageUrl);
            user.setEnabled(enabled);
            userRepository.save(user);
            LOGGER.debug("Updated user: {}", user);

            if (CollectionUtils.isNotEmpty(authorityNames)) {
                userAuthorityRepository.deleteByUserId(user.getId());
                authorityNames.forEach(authorityName -> {
                    userAuthorityRepository.insert(new UserAuthority(user.getId(), authorityName));
                });
                LOGGER.debug("Updated user authorities");
            }
        });
    }

    @Override
    public Optional<User> findOneByUserName(String userName) {
        Assert.hasText(userName, "it must not be null, empty, or blank");
        return userRepository.findOneByUserName(userName.toLowerCase(Locale.ENGLISH));
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
        return userRepository.findOneByUserNameOrEmailOrMobileNo(login.toLowerCase(Locale.ENGLISH),
                login.toLowerCase(Locale.ENGLISH), login.toLowerCase(Locale.ENGLISH));
    }

    @Override
    public Page<User> findByLogin(Pageable pageable, String login) {
        Assert.hasText(login, "it must not be null, empty, or blank");
        return userRepository.findByUserNameOrEmailOrMobileNo(pageable, login, login, login);
    }

    @Override
    public Optional<User> activateRegistration(String activationKey) {
        return userRepository.findOneByActivationKey(activationKey).map(user -> {
            user.setActivated(true);
            user.setActivationKey(null);
            userRepository.save(user);
            LOGGER.debug("Activated user: {}", user);
            return user;
        });
    }

    @Override
    public Optional<User> requestPasswordReset(String email, String resetKey) {
        return userRepository.findOneByEmailAndActivatedIsTrue(email).map(user -> {
            user.setResetKey(RandomUtils.generateResetKey());
            user.setResetTime(Instant.now());
            userRepository.save(user);
            LOGGER.debug("Requested reset user password for reset key {}", resetKey);
            return user;
        });
    }

    @Override
    public Optional<User> completePasswordReset(String newRawPassword, String resetKey) {
        return userRepository.findOneByResetKey(resetKey)
                .filter(user -> user.getResetTime().isAfter(Instant.now().minusSeconds(TimeUnit.DAYS.toSeconds(1))))
                .map(user -> {
                    user.setPasswordHash(passwordEncoder.encode(newRawPassword));
                    user.setResetKey(null);
                    user.setResetTime(null);
                    userRepository.save(user);
                    LOGGER.debug("Reset user password for reset key {}", resetKey);
                    return user;
                });
    }

    @Override
    public boolean checkValidPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) && password.length() >= ManagedUserDTO.RAW_PASSWORD_MIN_LENGTH
                && password.length() <= ManagedUserDTO.RAW_PASSWORD_MAX_LENGTH);
    }
}