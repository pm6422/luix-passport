package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.pojo.ManagedRole;
import cn.luixtech.passport.server.repository.RoleRepository;
import cn.luixtech.passport.server.service.RolePermissionService;
import cn.luixtech.passport.server.service.RoleService;
import com.alibaba.fastjson2.JSON;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class RoleController {
    private final RoleRepository        roleRepository;
    private final RoleService           roleService;
    private final RolePermissionService rolePermissionService;
    private final HttpHeaderCreator     httpHeaderCreator;

    @Operation(summary = "create new role")
    @PostMapping("/api/roles")
    public ResponseEntity<Void> create(@Parameter(description = "domain", required = true) @Valid @RequestBody ManagedRole domain) {
        roleService.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "find role list")
    @GetMapping("/api/roles")
    public ResponseEntity<List<Role>> find(@ParameterObject Pageable pageable,
                                           @Parameter(description = "ID") @RequestParam(value = "id", required = false) String id) {
        Page<Role> domains = roleService.find(pageable, id);
        HttpHeaders headers = generatePageHeaders(domains);
        return ResponseEntity.ok().headers(headers).body(domains.getContent());
    }

    @Operation(summary = "find all role IDs")
    @GetMapping("/api/roles/ids")
    public ResponseEntity<Set<String>> findAllIds() {
        return ResponseEntity.ok().body(roleService.findAllIds());
    }

    @Operation(summary = "find role by id")
    @GetMapping("/api/roles/{id}")
    public ResponseEntity<ManagedRole> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Role domain = roleRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        Set<String> permissionIds = rolePermissionService.findPermissionIds(Set.of(domain.getId()));
        return ResponseEntity.ok(ManagedRole.of(domain, permissionIds));
    }

    @Operation(summary = "update role")
    @PutMapping("/api/roles")
    public ResponseEntity<Void> update(@Parameter(description = "new role", required = true) @Valid @RequestBody ManagedRole domain) {
        roleService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getId())).build();
    }

    @Operation(summary = "delete role by id")
    @DeleteMapping("/api/roles/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        roleRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "import roles", description = "file format should be JSON")
    @PostMapping(value = "/api/roles/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void importData(@Parameter(description = "file", required = true) @RequestPart MultipartFile file) throws IOException {
        String jsonStr = StreamUtils.copyToString(file.getInputStream(), StandardCharsets.UTF_8);
        List<Role> records = JSON.parseArray(jsonStr, Role.class);
        List<Role> all = new ArrayList<>(records.size());
        records.forEach(record -> {
            record.setId(null);
            record.setCreatedAt(Instant.now());
            record.setModifiedAt(record.getCreatedAt());
            all.add(record);
        });
        roleRepository.saveAll(all);
    }
}
