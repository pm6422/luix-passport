package org.infinity.passport.service;

import org.infinity.passport.domain.User;
import org.infinity.passport.dto.UserNameAndPasswordDTO;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface UserService {

    void changePassword(UserNameAndPasswordDTO dto);

    User insert(User user, String rawPassword);

    void update(User dto);

    User findOneByUserName(String userName);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findOneByLogin(String login);

    Page<User> findByLogin(Pageable pageable, String login);

    Optional<User> activateRegistration(String activationKey);

    User requestPasswordReset(String email, String resetKey);

    User completePasswordReset(String newRawPassword, String resetKey);

    void deleteByUserName(String userName);
}