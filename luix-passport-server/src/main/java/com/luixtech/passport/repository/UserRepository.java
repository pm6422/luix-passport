package com.luixtech.passport.repository;

import com.luixtech.passport.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findOneByUsername(String username);

    Optional<User> findOneByUsernameLike(String username);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByEmailAndActivatedIsTrue(String email);

    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findOneByUsernameOrEmailOrMobileNo(String username, String email, String mobileNo);

    Page<User> findByUsernameOrEmailOrMobileNo(Pageable pageable, String username, String email, String mobileNo);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByActivationKey(String activationKey);

    Page<User> findByUsernameNot(Pageable pageable, String username);

    List<User> findByActivatedIsFalseAndCreatedTimeBefore(Instant createdTime);
}
