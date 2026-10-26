package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findOneByUsernameOrEmail(String username, String email);

    Optional<User> findOneByEmailAndActivated(String email, boolean activated);

    Optional<User> findOneByResetCode(String resetCode);

    Optional<User> findOneByActivationCode(String activationCode);

    List<User> findByEnabledIsTrue();

    int countByEmail(String email);

    int countByEmailAndUsernameNot(String email, String username);

    int countByMobileNoAndUsernameNot(String mobileNo, String username);

    long countByEnabledIsTrue();

    long countByCreatedAtAfter(Instant since);

    long countByMembershipType(String membershipType);

    long countByMembershipTypeIsNull();

    List<User> findByMembershipExpiresAtBeforeAndMembershipTypeNotNull(Instant now);
}
