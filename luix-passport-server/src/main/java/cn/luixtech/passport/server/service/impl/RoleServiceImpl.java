package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.repository.RoleRepository;
import cn.luixtech.passport.server.service.RoleService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Set;

import static cn.luixtech.passport.server.persistence.Tables.ROLE;

@Service
@AllArgsConstructor
public class RoleServiceImpl implements RoleService {
    private final RoleRepository roleRepository;
    private final DSLContext     dslContext;

    @Override
    public Set<String> findAllIds() {
        return dslContext.select(ROLE.ID).from(ROLE).fetchSet(ROLE.ID);
    }
}
