package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.config.oauth.AuthUser;
import cn.luixtech.passport.server.domain.SupportedDateTimeFormat;
import cn.luixtech.passport.server.domain.SupportedTimezone;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserRole;
import cn.luixtech.passport.server.exception.UserNotActivatedException;
import cn.luixtech.passport.server.persistence.Tables;
import cn.luixtech.passport.server.pojo.ManagedUser;
import cn.luixtech.passport.server.pojo.ProfileScopeUser;
import cn.luixtech.passport.server.repository.SupportedDateTimeFormatRepository;
import cn.luixtech.passport.server.repository.SupportedTimezoneRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.repository.UserRoleRepository;
import cn.luixtech.passport.server.service.UserRoleService;
import cn.luixtech.passport.server.service.UserService;
import cn.luixtech.passport.server.statemachine.UserEvent;
import cn.luixtech.passport.server.statemachine.UserState;
import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import com.luixtech.utilities.encryption.JasyptEncryptUtils;
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
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import reactor.core.publisher.Mono;

import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.TemporalUnit;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.BCRYPT_PASSWORD_ENCODER;
import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.DEFAULT_PASSWORD_ENCODER_PREFIX;
import static cn.luixtech.passport.server.controller.UserProfilePicController.USER_PHOTO_TOKEN_KEY;
import static cn.luixtech.passport.server.controller.UserProfilePicController.USER_PHOTO_URL;
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;
import static com.luixtech.utilities.encryption.JasyptEncryptUtils.DEFAULT_ALGORITHM;
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
    private final PasswordEncoder                    passwordEncoder;
    private final DSLContext                         dslContext;
    private final SupportedTimezoneRepository        supportedTimezoneRepository;
    private final SupportedDateTimeFormatRepository  supportedDateTimeFormatRepository;
    private final UserRepository                     userRepository;
    private final UserRoleRepository                 userRoleRepository;
    private final UserRoleService                    userRoleService;
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

        boolean accountNonExpired = user.getAccountExpiresAt() == null || LocalDateTime.now().isBefore(user.getAccountExpiresAt());
        boolean passwordNonExpired = user.getPasswordExpiresAt() == null || LocalDateTime.now().isBefore(user.getPasswordExpiresAt());

        Set<String> roles = findRoles(user.getId());
        List<GrantedAuthority> authorities = roles.stream().map(SimpleGrantedAuthority::new).collect(Collectors.toList());
        Set<String> teamIds = findOrgIds(user.getId());

        String modifiedTime = ISO_8601_EXTENDED_DATETIME_TIME_ZONE_FORMAT.format(user.getModifiedAt().atZone(ZoneId.systemDefault()).toInstant().toEpochMilli());

        String photoUrl = null;
        if (httpServletRequest != null) {
            photoUrl = getRequestUrl(httpServletRequest) + USER_PHOTO_URL + JasyptEncryptUtils.encrypt(user.getId(), DEFAULT_ALGORITHM, USER_PHOTO_TOKEN_KEY);
        }
        return new AuthUser(user.getId(), user.getUsername(), user.getEmail(), user.getMobileNo(), user.getFirstName(), user.getLastName(), user.getPasswordHash(), user.getEnabled(), accountNonExpired, passwordNonExpired, true, photoUrl, user.getLocale(), modifiedTime, authorities, roles, teamIds);
    }

    @Override
    public Optional<User> findOne(String loginName) {
//        User user = dslContext.selectFrom(Tables.USER)
//                .where(USER.USERNAME.eq(loginName))
//                .or(USER.EMAIL.eq(loginName))
//                .limit(1)
//                // Convert User Record to POJO User
//                .fetchOneInto(User.class);
        return userRepository.findOneByUsernameOrEmailOrMobileNo(loginName, loginName, loginName);
    }

    @Override
    public ManagedUser findById(String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        ManagedUser managedUser = new ManagedUser();
        BeanUtils.copyProperties(user, managedUser);
        managedUser.setRoles(findRoles(id));
        managedUser.setLocale(user.getLocale());
        managedUser.setTimezone(user.getTimeZone());
        managedUser.setPasswordHash("*");
        return managedUser;
    }

    @Override
    public ManagedUser findByEmail(String email) {
        User user = userRepository.findOneByEmail(email).orElseThrow(() -> new DataNotFoundException(email));
        ManagedUser managedUser = new ManagedUser();
        BeanUtils.copyProperties(user, managedUser);
        managedUser.setRoles(findRoles(user.getId()));
        managedUser.setLocale(user.getLocale());
        managedUser.setTimezone(user.getTimeZone());
        managedUser.setPasswordHash("*");
        return managedUser;
    }

    @Override
    public ProfileScopeUser findByUsername(String username) {
        User user = userRepository.findOneByUsername(username).orElseThrow(() -> new DataNotFoundException(username));
        return ProfileScopeUser.of(user.getUsername(), user.getEmail(), findRoles(user.getId()));
    }

    @Override
    public Set<String> findRoles(String userId) {
        return dslContext.select(Tables.USER_ROLE.ROLE).from(Tables.USER_ROLE).where(Tables.USER_ROLE.USER_ID.eq(userId)).fetchSet(Tables.USER_ROLE.ROLE);
    }

    @Override
    public Set<String> findOrgIds(String userId) {
        return dslContext.select(Tables.ORG_USER.ORG_ID).from(Tables.ORG_USER).where(Tables.ORG_USER.USER_ID.eq(userId)).fetchSet(Tables.ORG_USER.ORG_ID);
    }

    @Override
    public Set<String> findPermissions(String userId) {
        return dslContext.select(Tables.USER_PERMISSION.PERMISSION).from(Tables.USER_PERMISSION).where(Tables.USER_PERMISSION.USER_ID.eq(userId)).fetchSet(Tables.USER_PERMISSION.PERMISSION);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User insert(User domain, Set<String> authorities, String rawPassword, boolean permanentAccount) {
        // From pojo to record
//        UserRecord userRecord = dslContext.newRecord(USER, domain);

        if (userRepository.findOneByUsername(domain.getUsername().toLowerCase()).isPresent()) {
            throw new DuplicationException(Map.of("username", domain.getUsername()));
        }
        if (userRepository.findOneByEmail(domain.getEmail()).isPresent()) {
            throw new DuplicationException(Map.of("email", domain.getEmail()));
        }
        if (userRepository.findOneByMobileNo(domain.getMobileNo()).isPresent()) {
            throw new DuplicationException(Map.of("mobileNo", domain.getMobileNo()));
        }

        String id = IdGenerator.generateId();
        domain.setId(id);
        domain.setUsername(domain.getUsername().toLowerCase());
        domain.setEmail(domain.getEmail().toLowerCase());
        domain.setPasswordHash(DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(rawPassword));
        domain.setActivationCode(generateRandomCode());
        domain.setResetCode(null);
        domain.setResetAt(null);
        domain.setActivated(false);
        domain.setEnabled(true);
        domain.setPasswordExpiresAt(LocalDateTime.now().plusMonths(6));
        domain.setLocale(env.getProperty("spring.web.locale"));

        SupportedTimezone presetTimezone = supportedTimezoneRepository.findByPresetIsTrue()
                .orElseThrow(() -> new DataNotFoundException("preset timezone"));
        domain.setTimeZone(presetTimezone.getId());

        SupportedDateTimeFormat presetDateTimeFormat = supportedDateTimeFormatRepository.findByPresetIsTrue()
                .orElseThrow(() -> new DataNotFoundException("preset date time format"));
        domain.setDateTimeFormat(presetDateTimeFormat.getId());

        if (!permanentAccount) {
            domain.setAccountExpiresAt(LocalDateTime.now().plusDays(30));
        }

        userRepository.save(domain);
        stateMachine.startReactively().block();
        log.info("Created user: {}", domain);

        List<UserRole> userAuthorities = userRoleService.generate(id, authorities);
        userRoleRepository.saveAll(userAuthorities);
        return domain;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User update(User domain, Set<String> roles) {
        User updated = update(domain);
        userRoleService.update(domain.getId(), roles);
        return updated;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User update(User domain) {
        int existingEmailCount = userRepository.countByEmailAndIdNot(domain.getEmail(), domain.getId());
        if (existingEmailCount > 0) {
            throw new DuplicationException(Map.of("email", domain.getEmail()));
        }
        int existingMobileNoCount = userRepository.countByMobileNoAndIdNot(domain.getMobileNo(), domain.getId());
        if (existingMobileNoCount > 0) {
            throw new DuplicationException(Map.of("mobileNo", domain.getMobileNo()));
        }

        domain.setModifiedBy(AuthUtils.getCurrentUsername());
        domain.setModifiedAt(Instant.now());

        userRepository.save(domain);
        log.debug("Updated user: {}", domain);
        return domain;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User changePassword(String id, String oldRawPassword, String newRawPassword, String verificationCode) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        if (StringUtils.isNotEmpty(verificationCode)) {
            Validate.isTrue(verificationCode.equalsIgnoreCase(user.getVerificationCode()), "Invalid verification code!");
            Validate.isTrue(user.getVerificationCodeSentAt().plusDays(1).isAfter(LocalDateTime.now()), "Invalid verification exceeds one day before!");
        }
        if (StringUtils.isNotEmpty(oldRawPassword)) {
            try {
                if (!passwordEncoder.matches(oldRawPassword, user.getPasswordHash())) {
                    throw new IllegalArgumentException(messageCreator.getMessage("UE5008"));
                }
            } catch (Exception ex) {
                throw new IllegalArgumentException(messageCreator.getMessage("UE5008"));
            }
        }
        user.setPasswordHash(DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(newRawPassword));
        userRepository.save(user);
        log.info("Changed password for user: {}", user);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User requestEmailChangeVerificationCode(User user, String email) {
        user.setVerificationCode(generateRandomVerificationCode());
        user.setVerificationCodeSentAt(LocalDateTime.now());
        user.setNewEmail(email);
        userRepository.save(user);
        return user;
    }

    @Override
    public User requestPasswordChangeVerificationCode(User user) {
        user.setVerificationCode(generateRandomVerificationCode());
        user.setVerificationCodeSentAt(LocalDateTime.now());
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User requestPasswordRecovery(String email) {
        User user = userRepository.findOneByEmailAndActivated(email, true).orElseThrow(() -> new RuntimeException("Email does not exist"));

        user.setResetCode(generateRandomCode());
        user.setResetAt(LocalDateTime.now());

        userRepository.save(user);
        log.info("Requested password reset by reset code {}", user.getResetCode());
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void resetPassword(String resetCode, String newRawPassword) {
        User user = userRepository.findOneByResetCode(resetCode).orElseThrow(() -> new RuntimeException("Invalid reset code"));

        Validate.isTrue(LocalDateTime.now().isBefore(user.getResetAt().plusDays(1)), messageCreator.getMessage("UE1023"));

        user.setPasswordHash(DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(newRawPassword));
        user.setResetCode(null);
        user.setResetAt(null);

        userRepository.save(user);
        log.debug("Reset password by reset code {}", resetCode);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void activate(String activationCode) {
        User user = userRepository.findOneByActivationCode(activationCode).orElseThrow(() -> new RuntimeException("Invalid activation code"));

        user.setActivated(true);
        user.setActivationCode(null);
        userRepository.save(user);
        stateMachine.sendEvent(Mono.just(MessageBuilder.withPayload(UserEvent.ACTIVATE).build()));
        log.info("Activated user by activation code {}", activationCode);
    }

    @Override
    public Page<User> find(Pageable pageable, String username, String email, String mobileNo, Boolean enabled, Boolean activated) {
        // Ignore query parameter if it has a null value
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
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void deleteById(String userId) {
        userRepository.deleteById(userId);
    }

    @Override
    public String generateRandomCode() {
        return RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" + RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" + RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" + RandomStringUtils.randomAlphanumeric(4).toUpperCase() + "-" + RandomStringUtils.randomAlphanumeric(4).toUpperCase();
    }

    @Override
    public String generateRandomVerificationCode() {
        return RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphabetic(1).toUpperCase() + RandomStringUtils.randomAlphabetic(1).toUpperCase();
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public User extendAccountValidityPeriod(String id, long amountToAdd, TemporalUnit unit) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        if (user.getAccountExpiresAt().isBefore(LocalDateTime.now())) {
            user.setAccountExpiresAt(LocalDateTime.now().plus(amountToAdd, unit));
        } else {
            user.setAccountExpiresAt(user.getAccountExpiresAt().plus(amountToAdd, unit));
        }
        userRepository.save(user);
        return user;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, rollbackFor = Exception.class)
    public void changeToNewEmail(User currentUser) {
        currentUser.setEmail(currentUser.getNewEmail());
        currentUser.setNewEmail(StringUtils.EMPTY);
        currentUser.setVerificationCode(StringUtils.EMPTY);
        currentUser.setVerificationCodeSentAt(null);
        currentUser.setModifiedBy(AuthUtils.getCurrentUsername());
        currentUser.setModifiedAt(Instant.now());

        userRepository.save(currentUser);
    }
}
