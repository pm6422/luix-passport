package org.infinity.passport.service.impl;

import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.UserDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.RandomUtils;
import org.infinity.passport.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

import java.time.Instant;
import java.util.Locale;
import java.util.Optional;
import java.util.Set;
import java.util.concurrent.TimeUnit;

@Service
@Slf4j
public class UserServiceImpl implements UserService {

    private final UserRepository userRepository;

    private final UserAuthorityRepository userAuthorityRepository;

    private final PasswordEncoder passwordEncoder;

    public UserServiceImpl(UserRepository userRepository, UserAuthorityRepository userAuthorityRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.passwordEncoder = passwordEncoder;
    }

    // private void removeUserToken(User user) {
    // String clientId =
    // applicationProperties.getSecurity().getAuthentication().getOauth().getClientId();
    // tokenStore.findTokensByClientIdAndUserName(clientId,
    // user.getUserName()).stream()
    // .forEach(token -> tokenStore.removeAccessToken(token));
    // }

    @Override
    public void changePassword(String userName, String newRawPassword) {
        Assert.hasText(userName, "it must not be null, empty, or blank");
        Assert.hasText(newRawPassword, "it must not be null, empty, or blank");

        if (!checkValidPasswordLength(newRawPassword)) {
            throw new FieldValidationException("password", "password", "error.incorrect.password.length");
        }
        userRepository.findOneByUserName(userName).ifPresent(user -> {
            user.setPasswordHash(passwordEncoder.encode(newRawPassword));
            userRepository.save(user);
            log.debug("Changed password for User: {}", user);
        });
    }

    @Override
    public User insert(String userName, String rawPassword, String firstName, String lastName, String email,
                       String mobileNo, String activationKey, Boolean activated, Boolean enabled,
                       String remarks, String resetKey, Instant resetTime, Set<String> authorityNames) {

        if (findOneByUserName(userName).isPresent()) {
            throw new FieldValidationException("userDTO", "userName", userName,
                    "error.registration.user.exists", userName);
        }
        if (findOneByEmail(email).isPresent()) {
            throw new FieldValidationException("userDTO", "email", email,
                    "error.registration.email.exists", email);
        }
        if (findOneByMobileNo(mobileNo).isPresent()) {
            throw new FieldValidationException("userDTO", "mobileNo", mobileNo,
                    "error.registration.mobile.exists", mobileNo);
        }

        User newUser = new User();
        newUser.setUserName(userName.toLowerCase());
        newUser.setPasswordHash(passwordEncoder.encode(rawPassword));
        newUser.setFirstName(firstName);
        newUser.setLastName(lastName);
        newUser.setMobileNo(mobileNo);
        newUser.setEmail(email.toLowerCase());
        newUser.setActivated(activated);
        newUser.setActivationKey(activationKey);
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
            authorityNames.forEach(authorityName -> userAuthorityRepository.insert(new UserAuthority(newUser.getId(), authorityName)));
        }

        log.debug("Created Information for User: {}", newUser);
        return newUser;
    }

    @Override
    public void updateWithCheck(UserDTO dto) {
        Optional<User> existingUser = findOneByUserName(dto.getUserName());

        if (!existingUser.isPresent()) {
            throw new NoDataException(dto.getUserName());
        }
        existingUser = findOneByEmail(dto.getEmail());
        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(dto.getUserName()))) {
            throw new FieldValidationException("userDTO", "email", dto.getEmail(), "error.registration.email.exists",
                    dto.getEmail());
        }
        existingUser = findOneByMobileNo(dto.getMobileNo());
        if (existingUser.isPresent() && (!existingUser.get().getUserName().equalsIgnoreCase(dto.getUserName()))) {
            throw new FieldValidationException("userDTO", "mobileNo", dto.getMobileNo(),
                    "error.registration.mobile.exists", dto.getMobileNo());
        }
        if (existingUser.isPresent() && !Boolean.TRUE.equals(dto.getActivated())
                && Boolean.TRUE.equals(existingUser.get().getActivated())) {
            throw new FieldValidationException("userDTO", "activated", "error.change.active.to.inactive");
        }

        update(dto.getUserName().toLowerCase(), dto.getFirstName(), dto.getLastName(),
                dto.getEmail().toLowerCase(), dto.getMobileNo(), SecurityUtils.getCurrentUserName(), dto.getActivated(),
                dto.getEnabled(), dto.getRemarks(), dto.getAuthorities());
    }

    @Override
    public void update(String userName, String firstName, String lastName, String email, String mobileNo,
                       String modifiedBy, Boolean activated, Boolean enabled, String remarks, Set<String> authorityNames) {
        userRepository.findOneByUserName(userName.toLowerCase()).ifPresent(user -> {
            user.setFirstName(firstName);
            user.setLastName(lastName);
            user.setEmail(email.toLowerCase());
            user.setMobileNo(mobileNo);
            user.setModifiedBy(modifiedBy);
            user.setModifiedTime(Instant.now());
            user.setActivated(activated);
            user.setEnabled(enabled);
            userRepository.save(user);
            log.debug("Updated user: {}", user);

            if (CollectionUtils.isNotEmpty(authorityNames)) {
                userAuthorityRepository.deleteByUserId(user.getId());
                authorityNames.forEach(authorityName -> userAuthorityRepository.insert(new UserAuthority(user.getId(), authorityName)));
                log.debug("Updated user authorities");
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
            log.debug("Activated user: {}", user);
            return user;
        });
    }

    @Override
    public Optional<User> requestPasswordReset(String email, String resetKey) {
        return userRepository.findOneByEmailAndActivatedIsTrue(email).map(user -> {
            user.setResetKey(RandomUtils.generateResetKey());
            user.setResetTime(Instant.now());
            userRepository.save(user);
            log.debug("Requested reset user password for reset key {}", resetKey);
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
                    log.debug("Reset user password for reset key {}", resetKey);
                    return user;
                });
    }

    @Override
    public boolean checkValidPasswordLength(String password) {
        return (!StringUtils.isEmpty(password) && password.length() >= ManagedUserDTO.RAW_PASSWORD_MIN_LENGTH
                && password.length() <= ManagedUserDTO.RAW_PASSWORD_MAX_LENGTH);
    }
}