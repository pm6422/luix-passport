package cn.luixtech.passport.server.service;


import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.pojo.ManagedUser;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.temporal.TemporalUnit;
import java.util.Optional;
import java.util.Set;

public interface UserService {
    Optional<User> findOne(String loginName);

    ManagedUser findByUsername(String username);

    ManagedUser findByEmail(String email);

    Page<User> find(Pageable pageable, String username, String email, String mobileNo, Boolean enabled, Boolean activated);

    User insert(User domain, Set<String> roleIds, String rawPassword, boolean permanentAccount);

    User update(ManagedUser managedUser);

    void activate(String activationCode);

    User changePassword(String username, String oldRawPassword, String newRawPassword, String verificationCode);

    User requestEmailChangeVerificationCode(User user, String email);

    User requestPasswordChangeVerificationCode(User user);

    User requestPasswordRecovery(String email);

    void resetPassword(String resetCode, String newRawPassword);

    void changeToNewEmail(User currentUser);

    void deleteByUsername(String username);

    String generateRandomCode();

    String generateRandomVerificationCode();

    void extendValidityPeriod(String username, long amountToAdd, TemporalUnit unit);

    void updateLastLoginTime(String username);

    void cascadeUpdateUsername(String oldUsername, String newUsername);
}
