package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link RolePermission} entity.
 */
@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {

}
