package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data JPA repository for the {@link Permission} entity.
 */
@Repository
public interface PermissionRepository extends JpaRepository<Permission, String> {

}
