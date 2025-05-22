package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Permission;
import cn.luixtech.passport.server.repository.PermissionRepository;
import cn.luixtech.passport.server.service.PermissionService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class PermissionServiceImpl implements PermissionService {
    private final PermissionRepository permissionRepository;

    @Override
    public Page<Permission> find(Pageable pageable, String resourceType, String action) {
        // Ignore a query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Permission criteria = new Permission();
        criteria.setResourceType(resourceType);
        criteria.setAction(action);
        Example<Permission> queryExample = Example.of(criteria, matcher);
        return permissionRepository.findAll(queryExample, pageable);
    }
}
