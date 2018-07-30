package org.infinity.passport.repository;

import java.time.Instant;
import java.util.List;
import java.util.Optional;

import org.infinity.passport.domain.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the User entity.
 */
@Repository
public interface UserRepository extends MongoRepository<User, String> {

    Optional<User> findOneByUserName(String userName);

    Optional<User> findOneByUserNameLike(String userName);

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByEmailAndActivatedIsTrue(String email);

    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findOneByUserNameOrEmailOrMobileNo(String userName, String email, String mobileNo);

    Page<User> findByUserNameOrEmailOrMobileNo(Pageable pageable, String userName, String email, String mobileNo);

    Optional<User> findOneByResetKey(String resetKey);

    Optional<User> findOneByActivationKey(String activationKey);

    Page<User> findByUserNameNot(Pageable pageable, String userName);

    List<User> findByActivatedIsFalseAndCreatedTimeBefore(Instant createdTime);
}
