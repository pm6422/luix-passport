package com.luixtech.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import com.luixtech.passport.component.HttpHeaderCreator;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.Menu;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.exception.DuplicationException;
import com.luixtech.passport.repository.MenuRepository;
import com.luixtech.passport.service.MenuService;
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

import static com.luixtech.passport.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the admin menu.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class MenuController {
    private final MenuRepository    menuRepository;
    private final MenuService       menuService;
    private final HttpHeaderCreator httpHeaderCreator;

    @Operation(summary = "create menu")
    @PostMapping("/api/menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "menu", required = true) @Valid @RequestBody Menu entity) {
        log.debug("REST request to create admin menu: {}", entity);
        menuRepository.findOneByAppIdAndDepthAndSequence(entity.getAppId(), entity.getDepth(), entity.getSequence())
                .ifPresent((existingEntity) -> {
                    throw new DuplicationException(ImmutableMap.of("appName", entity.getAppId(), "level", entity.getDepth(), "sequence", entity.getSequence()));
                });
        menuRepository.save(entity);
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                        httpHeaderCreator.createSuccessHeader("SM1001", entity.getCode()))
                .build();
    }

    @Operation(summary = "find admin menu list")
    @GetMapping("/api/menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<Menu>> find(@ParameterObject Pageable pageable,
                                           @Parameter(description = "application name") @RequestParam(value = "appName", required = false) String appName) {
        Page<Menu> adminMenus = menuService.find(pageable, appName);
        HttpHeaders headers = generatePageHeaders(adminMenus);
        return ResponseEntity.ok().headers(headers).body(adminMenus.getContent());
    }

    @Operation(summary = "find admin menu by ID")
    @GetMapping("/api/menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Menu> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Menu domain = menuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update admin menu")
    @PutMapping("/api/menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new admin menu", required = true) @Valid @RequestBody Menu domain) {
        log.debug("REST request to update admin menu: {}", domain);
        menuRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        menuRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getCode())).build();
    }

    @Operation(summary = "delete admin menu by ID")
    @DeleteMapping("/api/menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete admin menu: {}", id);
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        menuRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", menu.getCode())).build();
    }

    @Operation(summary = "find admin menu by application name and level")
    @GetMapping("/api/menus/parents")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<Menu>> findParents(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName,
            @Parameter(description = "level", required = true) @RequestParam(value = "level") Integer level) {
        List<Menu> all = menuRepository.findByAppIdAndDepth(appName, level);
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "change-to-higher-order")
    @PutMapping("/api/menus/move-up/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void moveUp(@Parameter(description = "ID", required = true) @PathVariable String id) {
        menuService.moveUp(id);
    }

    @Operation(summary = "change-to-lower-order")
    @PutMapping("/api/menus/move-down/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void moveDown(@Parameter(description = "ID", required = true) @PathVariable String id) {
        menuService.moveDown(id);
    }

    @Operation(summary = "copy menus")
    @GetMapping("/api/menus/copy")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void copyMenus(@Parameter(description = "source application name", required = true, schema = @Schema(defaultValue = "Passport")) @RequestParam(value = "sourceAppName") String sourceAppName,
                          @Parameter(description = "target application name", required = true) @RequestParam(value = "targetAppName") String targetAppName) {
        List<Menu> sourceMenus = menuRepository.findByAppId(sourceAppName);
        sourceMenus.forEach(menu -> {
            menu.setAppId(targetAppName);
            menu.setId(null);
        });
        menuRepository.saveAll(sourceMenus);
    }

    @Operation(summary = "import menus", description = "import file format: use tabs to separate columns and carriage return between lines")
    @PostMapping(value = "/api/menus/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void importData(@Parameter(description = "file", required = true) @RequestPart MultipartFile file) throws IOException {
        List<String> lines = IOUtils.readLines(file.getInputStream(), StandardCharsets.UTF_8);
        List<Menu> list = new ArrayList<>();
        for (String line : lines) {
            if (StringUtils.isNotEmpty(line)) {
                String[] lineParts = line.split("\t");
                Menu entity = new Menu(lineParts[0], lineParts[1], lineParts[2],
                        Integer.parseInt(lineParts[3]), lineParts[4], Integer.parseInt(lineParts[5]), null);
                list.add(entity);
            }
        }
        menuRepository.saveAll(list);
    }
}
