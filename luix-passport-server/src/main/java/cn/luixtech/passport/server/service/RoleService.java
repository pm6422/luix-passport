package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.pojo.ManagedRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Set;

public interface RoleService {
    Page<Role> find(Pageable pageable, String id);

    Set<String> findAllIds();

    Role insert(ManagedRole managedRole);

    Role update(ManagedRole managedRole);
}
