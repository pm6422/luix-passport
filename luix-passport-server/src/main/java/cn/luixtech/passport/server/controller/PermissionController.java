package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.Permission;
import cn.luixtech.passport.server.repository.PermissionRepository;
import cn.luixtech.passport.server.service.PermissionService;
import com.alibaba.fastjson2.JSON;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
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

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class PermissionController {
    private final PermissionRepository permissionRepository;
    private final PermissionService    permissionService;

    @Operation(summary = "create new permission")
    @PostMapping("/api/permissions")
    public ResponseEntity<Void> create(@Parameter(description = "domain", required = true) @Valid @RequestBody Permission domain) {
        permissionRepository.save(domain);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    @Operation(summary = "find permission list")
    @GetMapping("/api/permissions")
    public ResponseEntity<List<Permission>> find(@ParameterObject Pageable pageable,
                                                 @RequestParam(value = "resourceType", required = false) String resourceType,
                                                 @RequestParam(value = "action", required = false) String action) {
        Page<Permission> domains = permissionService.find(pageable, resourceType, action);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find permission by id")
    @GetMapping("/api/permissions/{id}")
    public ResponseEntity<Permission> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Permission domain = permissionRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update permission")
    @PutMapping("/api/permissions")
    public ResponseEntity<Void> update(@Parameter(description = "domain", required = true) @Valid @RequestBody Permission domain) {
        permissionRepository.save(domain);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "delete permission by id")
    @DeleteMapping("/api/permissions/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        permissionRepository.deleteById(id);
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "import permissions", description = "file format should be JSON")
    @PostMapping(value = "/api/permissions/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void importData(@Parameter(description = "file", required = true) @RequestPart MultipartFile file) throws IOException {
        String jsonStr = StreamUtils.copyToString(file.getInputStream(), StandardCharsets.UTF_8);
        List<Permission> records = JSON.parseArray(jsonStr, Permission.class);
        List<Permission> all = new ArrayList<>(records.size());
        records.forEach(record -> {
            record.setId(null);
            record.setCreatedAt(Instant.now());
            record.setModifiedAt(record.getCreatedAt());
            all.add(record);
        });
        permissionRepository.saveAll(all);
    }
}
