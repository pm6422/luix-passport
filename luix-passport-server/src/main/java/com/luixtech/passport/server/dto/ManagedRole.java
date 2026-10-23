package com.luixtech.passport.server.dto;

import com.luixtech.passport.server.domain.Role;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.beans.BeanUtils;

import java.time.Instant;
import java.util.Set;

@Data
@NoArgsConstructor
public class ManagedRole {
    private   String      id;
    protected Instant     createdAt;
    protected String      createdBy;
    private   Instant     modifiedAt;
    private   String      modifiedBy;
    private   String      remark;
    private   Set<String> permissionIds;

    /**
     * Copies properties from the given Role to this ManagedRole.
     *
     * @param role the source object
     * @return the target object
     */
    public static ManagedRole of(Role role, Set<String> permissionIds) {
        ManagedRole managedRole = new ManagedRole();
        BeanUtils.copyProperties(role, managedRole);
        managedRole.setPermissionIds(permissionIds);
        return managedRole;
    }

    public Role toRole() {
        Role role = new Role();
        BeanUtils.copyProperties(this, role);
        return role;
    }
}
