package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.Permission;
import org.springframework.data.jpa.repository.JpaRepository;

public interface PermissionRepository extends JpaRepository<Permission, String> {

}
