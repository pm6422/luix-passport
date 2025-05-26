package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.service.RolePermissionService;
import lombok.AllArgsConstructor;
import org.jooq.DSLContext;
import org.springframework.stereotype.Service;

import java.util.Set;

import static cn.luixtech.passport.server.persistence.Tables.ROLE_PERMISSION;
import static cn.luixtech.passport.server.persistence.Tables.USER_ROLE;

@Service
@AllArgsConstructor
public class RolePermissionServiceImpl implements RolePermissionService {
    private final DSLContext dslContext;

    @Override
    public Set<String> findPermissionIds(Set<String> roleIds) {
        return dslContext.selectDistinct(ROLE_PERMISSION.PERMISSION_ID)
                .from(ROLE_PERMISSION)
                .join(USER_ROLE).on(ROLE_PERMISSION.ROLE_ID.eq(USER_ROLE.ROLE_ID))
                .where(USER_ROLE.ROLE_ID.in(roleIds))
                .fetchSet(ROLE_PERMISSION.PERMISSION_ID);
    }
}
