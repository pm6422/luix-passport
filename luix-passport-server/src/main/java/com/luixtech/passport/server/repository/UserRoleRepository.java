package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Set;

public interface UserRoleRepository extends JpaRepository<UserRole, String> {

    List<UserRole> findByUsername(String username);

    void deleteByUsername(String username);

    void deleteByUsernameAndRoleIdIn(String username, Set<String> rolesToDelete);
}
