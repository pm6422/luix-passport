package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.Role;
import cn.luixtech.passport.server.repository.RoleRepository;
import cn.luixtech.passport.server.service.RoleService;
import com.alibaba.fastjson2.JSON;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
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
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;

@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class RoleController {
    private final RoleRepository roleRepository;
    private final RoleService    roleService;

    @Operation(summary = "save role")
    @PostMapping("/api/roles")
    public ResponseEntity<Void> save(@Parameter(description = "domain", required = true) @Valid @RequestBody Role domain) {
        roleRepository.save(domain);
        return ResponseEntity.status(HttpStatus.OK).build();
    }

    @Operation(summary = "find all role IDs")
    @GetMapping("/api/roles/ids")
    public ResponseEntity<Set<String>> findAllIds() {
        return ResponseEntity.ok().body(roleService.findAllIds());
    }

    @Operation(summary = "find role by id")
    @GetMapping("/api/roles/{id}")
    public ResponseEntity<Role> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Role domain = roleRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
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
