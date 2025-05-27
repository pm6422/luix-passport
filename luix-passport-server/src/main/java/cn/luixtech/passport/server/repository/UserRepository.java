package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link User} entity.
 */
@Repository
public interface UserRepository extends JpaRepository<User, String> {

    Optional<User> findOneByEmail(String email);

    Optional<User> findOneByMobileNo(String mobileNo);

    Optional<User> findOneByUsernameOrEmailOrMobileNo(String username, String email, String mobileNo);

    Optional<User> findOneByEmailAndActivated(String email, boolean activated);

    Optional<User> findOneByResetCode(String resetCode);

    Optional<User> findOneByActivationCode(String activationCode);

    List<User> findByEnabledIsTrue();

    int countByEmailAndUsernameNot(String email, String username);

    int countByMobileNoAndUsernameNot(String mobileNo, String username);

    long countByEnabledIsTrue();

}
