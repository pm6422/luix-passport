package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the admin menu.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AdminMenuController {
    private final AdminMenuRepository adminMenuRepository;
    private final AdminMenuService    adminMenuService;
    private final HttpHeaderCreator   httpHeaderCreator;

    @Operation(summary = "create menu")
    @PostMapping("/api/admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "menu", required = true) @Valid @RequestBody AdminMenu entity) {
        log.debug("REST request to create admin menu: {}", entity);
        adminMenuRepository.findOneByAppNameAndLevelAndSequence(entity.getAppName(), entity.getLevel(), entity.getSequence())
                .ifPresent((existingEntity) -> {
                    throw new DuplicationException(ImmutableMap.of("appName", entity.getAppName(), "level", entity.getLevel(), "sequence", entity.getSequence()));
                });
        adminMenuRepository.insert(entity);
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                        httpHeaderCreator.createSuccessHeader("SM1001", entity.getCode()))
                .build();
    }

    @Operation(summary = "find admin menu list")
    @GetMapping("/api/admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AdminMenu>> find(@ParameterObject Pageable pageable,
                                                @Parameter(description = "application name") @RequestParam(value = "appName", required = false) String appName) {
        Page<AdminMenu> adminMenus = adminMenuService.find(pageable, appName);
        HttpHeaders headers = generatePageHeaders(adminMenus);
        return ResponseEntity.ok().headers(headers).body(adminMenus.getContent());
    }

    @Operation(summary = "find admin menu by ID")
    @GetMapping("/api/admin-menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<AdminMenu> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        AdminMenu domain = adminMenuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update admin menu")
    @PutMapping("/api/admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new admin menu", required = true) @Valid @RequestBody AdminMenu domain) {
        log.debug("REST request to update admin menu: {}", domain);
        adminMenuRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        adminMenuRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getCode())).build();
    }

    @Operation(summary = "delete admin menu by ID")
    @DeleteMapping("/api/admin-menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete admin menu: {}", id);
        AdminMenu adminMenu = adminMenuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        adminMenuRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", adminMenu.getCode())).build();
    }

    @Operation(summary = "find admin menu by application name and level")
    @GetMapping("/api/admin-menus/parents")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AdminMenu>> findParents(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName,
            @Parameter(description = "level", required = true) @RequestParam(value = "level") Integer level) {
        List<AdminMenu> all = adminMenuRepository.findByAppNameAndLevel(appName, level);
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "change-to-higher-order")
    @PutMapping("/api/admin-menus/move-up/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void moveUp(@Parameter(description = "ID", required = true) @PathVariable String id) {
        adminMenuService.moveUp(id);
    }

    @Operation(summary = "change-to-lower-order")
    @PutMapping("/api/admin-menus/move-down/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void moveDown(@Parameter(description = "ID", required = true) @PathVariable String id) {
        adminMenuService.moveDown(id);
    }

    @Operation(summary = "cope admin menu")
    @GetMapping("/api/admin-menus/copy")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void copyMenus(@Parameter(description = "source application name", required = true, schema = @Schema(defaultValue = "Passport")) @RequestParam(value = "sourceAppName") String sourceAppName,
                          @Parameter(description = "target application name", required = true) @RequestParam(value = "targetAppName") String targetAppName) {
        List<AdminMenu> sourceMenus = adminMenuRepository.findByAppName(sourceAppName);
        sourceMenus.forEach(menu -> {
            menu.setAppName(targetAppName);
            menu.setId(null);
        });
        adminMenuRepository.saveAll(sourceMenus);
    }

    @Operation(summary = "import admin menus", description = "import file format: use tabs to separate columns and carriage return between lines")
    @PostMapping(value = "/api/admin-menus/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void importData(@Parameter(description = "file", required = true) @RequestPart MultipartFile file) throws IOException {
        List<String> lines = IOUtils.readLines(file.getInputStream(), StandardCharsets.UTF_8);
        List<AdminMenu> list = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                String[] lineParts = line.split("\t");
                AdminMenu entity = new AdminMenu(lineParts[0], lineParts[1], lineParts[2],
                        Integer.parseInt(lineParts[3]), lineParts[4], Integer.parseInt(lineParts[5]), null);
                list.add(entity);
            }
        }
        adminMenuRepository.insert(list);
    }
}
