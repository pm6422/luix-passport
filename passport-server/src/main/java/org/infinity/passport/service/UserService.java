package org.infinity.passport.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.infinity.passport.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserService {

    void changePassword(String userName, String newRawPassword);

    String getPasswordHash(String rawPassword);

    User insert(String userName, String rawPassword, String firstName, String lastName, String email, String mobileNo,
            String activationKey, Boolean activated, String avatarImageUrl, Boolean enabled, String resetKey,
            Instant resetTime, Set<String> authorityNames);

    void update(String userName, String firstName, String lastName, String email, String mobileNo, String modifiedBy,
            Boolean activated, String avatarImageUrl, Boolean enabled, Set<String> authorityNames);

    Optional<User> findOneByUserName(String userName);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findOneByLogin(String login);

    Page<User> findByLogin(Pageable pageable, String login);

    Optional<User> activateRegistration(String activationKey);

    Optional<User> requestPasswordReset(String email, String resetKey);

    Optional<User> completePasswordReset(String newRawPassword, String resetKey);

    boolean checkValidPasswordLength(String password);

}