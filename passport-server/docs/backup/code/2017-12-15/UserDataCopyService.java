package org.infinity.passport.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Set;

import org.infinity.passport.domain.User;

public interface UserDataCopyService {

    User insert(String userName, String rawPassword, String firstName, String lastName, String email, String mobileNo,
            String activationKey, Boolean activated, String avatarImageUrl, Boolean enabled, String resetKey,
            Instant resetTime, Set<String> authorityNames);

    void update(String userName, String firstName, String lastName, String email, String mobileNo, String modifiedBy,
            Boolean activated, String avatarImageUrl, Boolean enabled, Set<String> authorityNames);

    Optional<User> findOneByLogin(String login);

}
