package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.UserPermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link UserPermission} entity.
 */
@Repository
public interface UserPermissionRepository extends JpaRepository<UserPermission, String> {

}
