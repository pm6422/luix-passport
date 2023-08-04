package com.luixtech.passport.controller;

import com.google.common.collect.ImmutableMap;
import com.luixtech.framework.component.HttpHeaderCreator;
import com.luixtech.passport.domain.App;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.Menu;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.exception.DuplicationException;
import com.luixtech.passport.repository.AppRepository;
import com.luixtech.passport.repository.MenuRepository;
import com.luixtech.passport.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
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

import static com.luixtech.framework.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.domain.Menu.EMPTY_MENU_ID;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the menu.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class MenuController {
    private final MenuRepository    menuRepository;
    private final AppRepository     appRepository;
    private final MenuService       menuService;
    private final HttpHeaderCreator httpHeaderCreator;

    @Operation(summary = "create menu")
    @PostMapping("/api/menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "menu", required = true) @Valid @RequestBody Menu domain) {
        log.debug("REST request to create menu: {}", domain);
        menuRepository.findOneByAppIdAndDepthAndSequence(domain.getAppId(), domain.getDepth(), domain.getSequence())
                .ifPresent((existingEntity) -> {
                    throw new DuplicationException(ImmutableMap.of("appName", domain.getAppId(), "level", domain.getDepth(), "sequence", domain.getSequence()));
                });
        App app = appRepository.findById(domain.getAppId()).orElseThrow(() -> new DataNotFoundException(domain.getAppId()));
        domain.setAppName(app.getName());
        menuRepository.save(domain);
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                        httpHeaderCreator.createSuccessHeader("SM1001", domain.getCode()))
                .build();
    }

    @Operation(summary = "find menu list")
    @GetMapping("/api/menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<Menu>> find(@ParameterObject Pageable pageable,
                                           @Parameter(description = "application id") @RequestParam(value = "appId", required = false) String appId,
                                           @Parameter(description = "depth") @RequestParam(value = "depth", required = false) Integer depth) {
        Page<Menu> domains = menuService.find(pageable, appId, depth);
        HttpHeaders headers = generatePageHeaders(domains);
        return ResponseEntity.ok().headers(headers).body(domains.getContent());
    }

    @Operation(summary = "find menu by ID")
    @GetMapping("/api/menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Menu> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Menu domain = menuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update menu")
    @PutMapping("/api/menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new menu", required = true) @Valid @RequestBody Menu domain) {
        log.debug("REST request to update menu: {}", domain);
        menuRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        App app = appRepository.findById(domain.getAppId()).orElseThrow(() -> new DataNotFoundException(domain.getAppId()));
        domain.setAppName(app.getName());
        menuRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getCode())).build();
    }

    @Operation(summary = "delete menu by ID", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete menu: {}", id);
        Menu menu = menuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        menuRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", menu.getCode())).build();
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
        List<Menu> sourceMenus = menuRepository.findByAppName(sourceAppName);
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
                        Integer.parseInt(lineParts[3]), lineParts[4], Integer.parseInt(lineParts[5]), EMPTY_MENU_ID);
                list.add(entity);
            }
        }
        menuRepository.saveAll(list);
    }
}
