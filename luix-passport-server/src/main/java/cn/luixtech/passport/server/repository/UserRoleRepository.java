package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

/**
 * Spring Data JPA repository for the {@link UserRole} entity.
 */
@Repository
public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    List<UserRole> findByUsername(String username);

    void deleteByUsername(String username);

    void deleteByUsernameAndRoleIdIn(String username, Set<String> rolesToDelete);
}
