package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.config.ApplicationProperties;
import cn.luixtech.passport.server.config.oauth.AuthUser;
import cn.luixtech.passport.server.domain.SupportedDateTimeFormat;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserRole;
import cn.luixtech.passport.server.exception.UserNotActivatedException;
import cn.luixtech.passport.server.pojo.ManagedUser;
import cn.luixtech.passport.server.repository.SupportedDateTimeFormatRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.repository.UserRoleRepository;
import cn.luixtech.passport.server.service.*;
import cn.luixtech.passport.server.statemachine.UserEvent;
import cn.luixtech.passport.server.statemachine.UserState;
import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import com.luixtech.utilities.exception.DuplicationException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.jooq.DSLContext;
import org.springframework.beans.BeanUtils;
import org.springframework.core.env.Environment;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.statemachine.StateMachine;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;
import java.time.temporal.TemporalUnit;
import java.util.*;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.BCRYPT_PASSWORD_ENCODER;
import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.DEFAULT_PASSWORD_ENCODER_PREFIX;
import static cn.luixtech.passport.server.persistence.Tables.USER;
import static org.apache.commons.lang3.time.DateFormatUtils.ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT;

/**
 * Authenticate a user from the database.
 * <p>
 * Refer below to review match password
 * DaoAuthenticationProvider#additionalAuthenticationChecks
 */
@Slf4j
@Service
@AllArgsConstructor
public class UserServiceImpl implements UserService, UserDetailsService {
    private final ApplicationProperties              applicationProperties;
    private final PasswordEncoder                    passwordEncoder;
    private final DSLContext                         dslContext;
    private final SupportedDateTimeFormatRepository  supportedDateTimeFormatRepository;
    private final UserRepository                     userRepository;
    private final UserRoleRepository                 userRoleRepository;
    private final UserRoleService                    userRoleService;
    private final UserNotificationService            userNotificationService;
    private final RolePermissionService              rolePermissionService;
    private final UserProfilePicService              userProfilePicService;
    private final TeamUserService                    teamUserService;
    private final MessageCreator                     messageCreator;
    private final HttpServletRequest                 httpServletRequest;
    private final Environment                        env;
    private final StateMachine<UserState, UserEvent> stateMachine;

    @Override
    public UserDetails loadUserByUsername(final String loginName) {
        log.info("Authenticating {}", loginName);
        if (StringUtils.isEmpty(loginName)) {
            log.warn("Login name must not be empty!");
            throw new BadCredentialsException("Login name must not be empty");
        }
        User user = findOne(loginName).orElseThrow(() -> new UsernameNotFoundException("User " + loginName + " was not found"));
        if (!Boolean.TRUE.equals(user.getActivated())) {
            throw new UserNotActivatedException(loginName);
        }

        boolean accountNonExpired = user.getAccountExpiresAt() == null || Instant.now().isBefore(user.getAccountExpiresAt());
        boolean passwordNonExpired = user.getPasswordExpiresAt() == null || Instant.now().isBefore(user.getPasswordExpiresAt());

        Set<String> roleIds = userRoleService.findRoleIds(user.getUsername());
        Set<String> permissionIds = rolePermissionService.findPermissionIds(roleIds);
        Set<String> orgIds = teamUserService.findTeamIdsByUsername(user.getUsername());

        List<GrantedAuthority> authorities = roleIds.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());

        String modifiedTime = ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(user.getModifiedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        String profilePicUrl = null;
        if (httpServletRequest != null) {
            profilePicUrl = userProfilePicService.buildProfilePicUrl(user.getUsername(), httpServletRequest);
        }
        return new AuthUser(user.getUsername(), user.getEmail(), user.getMobileNo(), user.getFirstName(),
                user.getLastName(), user.getPasswordHash(), user.getEnabled(), accountNonExpired, passwordNonExpired,
                true, profilePicUrl, user.getLocale(), modifiedTime, authorities, roleIds, permissionIds, orgIds);
    }

    @Override
    public User getCurrentUser() {
        return userRepository.findById(AuthUtils.getCurrentUsername())
                .orElseThrow(() -> new DataNotFoundException(AuthUtils.getCurrentUsername()));
    }

    @Override
    public Optional<User> findOne(String loginName) {
//        User user = dslContext.selectFrom(USER)
//                .where(USER.USERNAME.eq(loginName))
//                .or(USER.EMAIL.eq(loginName))
//                .limit(1)
//                // Convert User Record to POJO User
//                .fetchOneInto(User.class);
        return userRepository.findOneByUsernameOrEmail(loginName, loginName);
    }

    @Override
    public ManagedUser findByUsername(String username) {
        User user = userRepository.findById(username).orElseThrow(() -> new DataNotFoundException(username));
        return buildManagedUser(user, username);
    }

    @Override
    public ManagedUser findByEmail(String email) {
        User user = userRepository.findOneByEmail(email).orElseThrow(() -> new DataNotFoundException(email));
        return buildManagedUser(user, user.getUsername());
    }

    @Override
    public Page<User> find(Pageable pageable, String username, String email, String mobileNo, Boolean enabled, Boolean activated) {
        // Ignore a query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        User criteria = new User();
        criteria.setUsername(username);
        criteria.setEmail(email);
        criteria.setMobileNo(mobileNo);
        criteria.setEnabled(enabled);
        criteria.setActivated(activated);
        Example<User> queryExample = Example.of(criteria, matcher);
        return userRepository.findAll(queryExample, pageable);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User insert(User domain, Set<String> roleIds, String rawPassword, boolean permanentAccount) {
        if (userRepository.findById(domain.getUsername().toLowerCase()).isPresent()) {
            throw new DuplicationException(Map.of("username", domain.getUsername()));
        }
        if (userRepository.findOneByEmail(domain.getEmail()).isPresent()) {
            throw new DuplicationException(Map.of("email", domain.getEmail()));
        }
        if (userRepository.findOneByMobileNo(domain.getMobileNo()).isPresent()) {
            throw new DuplicationException(Map.of("mobileNo", domain.getMobileNo()));
        }

        domain.setUsername(domain.getUsername().toLowerCase());
        domain.setEmail(domain.getEmail().toLowerCase());
        domain.setPasswordHash(DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(rawPassword));
        domain.setActivationCode(generateRandomCode());
        domain.setResetCode(null);
        domain.setResetAt(null);
        domain.setActivated(false);
        domain.setEnabled(true);
        domain.setPasswordExpiresAt(Instant.now().plus(90, ChronoUnit.DAYS));
        domain.setLocale(env.getProperty("spring.web.locale"));

        domain.setTimeZoneId(applicationProperties.getTimezone().getDefaultTimezone());

        SupportedDateTimeFormat presetDateTimeFormat = supportedDateTimeFormatRepository.findByPresetIsTrue()
                .orElseThrow(() -> new DataNotFoundException("preset date time format"));
        domain.setDateTimeFormatId(presetDateTimeFormat.getId());

        if (!permanentAccount) {
            domain.setAccountExpiresAt(Instant.now().plus(30, ChronoUnit.DAYS));
        }

        userRepository.save(domain);
        stateMachine.startReactively().block();
        log.info("Created user: {}", domain);

        List<UserRole> userRoles = userRoleService.assignWithDefaults(domain.getUsername(), roleIds);
        userRoleRepository.saveAll(userRoles);
        return domain;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User update(ManagedUser managedUser) {
        int existingEmailCount = userRepository.countByEmailAndUsernameNot(managedUser.getEmail(), managedUser.getUsername());
        if (existingEmailCount > 0) {
            throw new DuplicationException(Map.of("email", managedUser.getEmail()));
        }
        int existingMobileNoCount = userRepository.countByMobileNoAndUsernameNot(managedUser.getMobileNo(), managedUser.getUsername());
        if (existingMobileNoCount > 0) {
            throw new DuplicationException(Map.of("mobileNo", managedUser.getMobileNo()));
        }

        User existingOne = userRepository.findById(managedUser.getUsername()).orElseThrow(() -> new DataNotFoundException(managedUser.getUsername()));
        existingOne.setFirstName(managedUser.getFirstName());
        existingOne.setLastName(managedUser.getLastName());
        existingOne.setLocale(managedUser.getLocale());
        existingOne.setTimeZoneId(managedUser.getTimeZoneId());
        existingOne.setDateTimeFormatId(managedUser.getDateTimeFormatId());

        if (managedUser.getEmail() != null) {
            existingOne.setEmail(managedUser.getEmail().toLowerCase());
        }
        if (managedUser.getMobileNo() != null) {
            existingOne.setMobileNo(managedUser.getMobileNo());
        }
        if (managedUser.getRemark() != null) {
            existingOne.setRemark(managedUser.getRemark());
        }
        if (managedUser.getEnabled() != null) {
            existingOne.setEnabled(managedUser.getEnabled());
        }
        if (managedUser.getRoleIds() != null) {
            userRoleService.update(managedUser.getUsername(), managedUser.getRoleIds());
        }

        userRepository.save(existingOne);
        log.debug("Updated user: {}", existingOne);
        return existingOne;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User changePassword(String username, String oldRawPassword, String newRawPassword, String verificationCode) {
        User user = userRepository.findById(username).orElseThrow(() -> new DataNotFoundException(username));
        if (StringUtils.isNotEmpty(verificationCode)) {
            Validate.isTrue(verificationCode.equalsIgnoreCase(user.getVerificationCode()), "Invalid verification code!");
            Validate.isTrue(user.getVerificationCodeSentAt().plus(1, ChronoUnit.DAYS).isAfter(Instant.now()), "Invalid verification exceeds one day before!");
        }
        if (StringUtils.isNotEmpty(oldRawPassword)) {
            Validate.isTrue(passwordEncoder.matches(oldRawPassword, user.getPasswordHash()), messageCreator.getMessage("UE5008"));
        }
        user.setPasswordHash(DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(newRawPassword));
        userRepository.save(user);
        log.info("Changed password for user: {}", user);
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User requestEmailChangeVerificationCode(User user, String email) {
        user.setVerificationCode(generateRandomVerificationCode());
        user.setVerificationCodeSentAt(Instant.now());
        user.setNewEmail(email);
        userRepository.save(user);
        userNotificationService.sendPersonalNotification(Collections.singletonList(user.getUsername()),
                "Change email", "You have requested to change your email.");
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User requestPasswordChangeVerificationCode(User user) {
        user.setVerificationCode(generateRandomVerificationCode());
        user.setVerificationCodeSentAt(Instant.now());
        userRepository.save(user);
        userNotificationService.sendPersonalNotification(Collections.singletonList(user.getUsername()),
                "Change password", "You have requested to change your password.");
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public User requestPasswordRecovery(String email) {
        User user = userRepository.findOneByEmailAndActivated(email, true).orElseThrow(() -> new RuntimeException("Email does not exist"));

        user.setResetCode(generateRandomCode());
        user.setResetAt(Instant.now());

        userRepository.save(user);
        log.info("Requested password reset by reset code {}", user.getResetCode());
        return user;
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(String resetCode, String newRawPassword) {
        User user = userRepository.findOneByResetCode(resetCode).orElseThrow(() -> new RuntimeException("Invalid or expired reset code"));

        Validate.isTrue(Instant.now().isBefore(user.getResetAt().plus(30, ChronoUnit.DAYS)), messageCreator.getMessage("UE1023"));

        user.setPasswordHash(DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(newRawPassword));
        user.setResetCode(null);
        user.setResetAt(null);

        userRepository.save(user);
        log.debug("Reset password by reset code {}", resetCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void changeToNewEmail(User currentUser) {
        currentUser.setEmail(currentUser.getNewEmail());
        currentUser.setNewEmail(StringUtils.EMPTY);
        currentUser.setVerificationCode(StringUtils.EMPTY);
        currentUser.setVerificationCodeSentAt(null);

        userRepository.save(currentUser);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void activate(String activationCode) {
        User user = userRepository.findOneByActivationCode(activationCode).orElseThrow(() -> new RuntimeException("Invalid activation code"));

        user.setActivated(true);
        user.setActivationCode(null);
        userRepository.save(user);
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(UserEvent.ACTIVATE).build()));

        userNotificationService.sendPersonalNotification(Collections.singletonList(user.getUsername()),
                "Activated account",
                "You have successfully activated the account, please contact the administrator to grant appropriate roles.");
        log.info("Activated user by activation code {}", activationCode);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void deleteByUsername(String username) {
        // cascade delete user and user related entities
        userRepository.deleteById(username);
    }

    @Override
    public String generateRandomCode() {
        return RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" +
                RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" +
                RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" +
                RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" +
                RandomStringUtils.randomAlphanumeric(4).toUpperCase();
    }

    @Override
    public String generateRandomVerificationCode() {
        return RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase() +
                RandomStringUtils.randomAlphabetic(1).toUpperCase();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void extendValidityPeriod(String username, long amountToAdd, TemporalUnit unit) {
        User user = userRepository.findById(username).orElseThrow(() -> new DataNotFoundException(username));
        Instant expiresAt = null;
        if (user.getAccountExpiresAt().isBefore(Instant.now())) {
            expiresAt = Instant.now().plus(amountToAdd, unit);
        } else {
            expiresAt = user.getAccountExpiresAt().plus(amountToAdd, unit);
        }
        dslContext
                .update(USER)
                .set(USER.ACCOUNT_EXPIRES_AT, expiresAt)
                .set(USER.MODIFIED_AT, Instant.now())
                .set(USER.MODIFIED_BY, AuthUtils.getCurrentUsername())
                .where(USER.USERNAME.eq(username))
                .execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void updateLastLoginTime(String username) {
        dslContext
                .update(USER)
                .set(USER.LAST_SIGN_IN_AT, Instant.now())
                .set(USER.MODIFIED_AT, Instant.now())
                .set(USER.MODIFIED_BY, AuthUtils.getCurrentUsername())
                .where(USER.USERNAME.eq(username))
                .execute();
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void cascadeUpdateUsername(String oldUsername, String newUsername) {
        // cascade update username of the user and the foreign key constraints entities
        dslContext
                .update(USER)
                .set(USER.USERNAME, newUsername)
                .set(USER.MODIFIED_AT, Instant.now())
                .set(USER.MODIFIED_BY, AuthUtils.getCurrentUsername())
                .where(USER.USERNAME.eq(oldUsername))
                .execute();
    }

    private ManagedUser buildManagedUser(User user, String username) {
        ManagedUser managedUser = new ManagedUser();
        BeanUtils.copyProperties(user, managedUser);
        Set<String> roleIds = userRoleService.findRoleIds(username);
        managedUser.setRoleIds(roleIds);
        Set<String> permissionIds = rolePermissionService.findPermissionIds(roleIds);
        managedUser.setPermissionIds(permissionIds);
        managedUser.setPasswordHash("*");
        return managedUser;
    }
}
