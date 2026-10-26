package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.Role;
import org.springframework.data.jpa.repository.JpaRepository;

public interface RoleRepository extends JpaRepository<Role, String> {

}
