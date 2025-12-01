package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.RolePermission;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Set;

@Repository
public interface RolePermissionRepository extends JpaRepository<RolePermission, String> {

    List<RolePermission> findByRoleId(String roleId);

    void deleteByRoleIdAndPermissionIdIn(String roleId, Set<String> permissionsToDelete);


}
