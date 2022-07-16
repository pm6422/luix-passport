package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.tags.Tag;
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
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the admin menu.
 */
@RestController
@Tag(name = "管理菜单")
@Slf4j
public class AdminMenuController {

    @Resource
    private AdminMenuRepository adminMenuRepository;
    @Resource
    private AdminMenuService    adminMenuService;
    @Resource
    private HttpHeaderCreator   httpHeaderCreator;

    @Operation(summary = "创建菜单")
    @PostMapping("/api/admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "菜单", required = true) @Valid @RequestBody AdminMenu entity) {
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

    @Operation(summary = "分页检索菜单列表")
    @GetMapping("/api/admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AdminMenu>> find(Pageable pageable,
                                                @Parameter(description = "应用名称") @RequestParam(value = "appName", required = false) String appName) {
        Page<AdminMenu> adminMenus = adminMenuService.find(pageable, appName);
        HttpHeaders headers = generatePageHeaders(adminMenus);
        return ResponseEntity.ok().headers(headers).body(adminMenus.getContent());
    }

    @Operation(summary = "根据ID检索菜单")
    @GetMapping("/api/admin-menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<AdminMenu> findById(@Parameter(description = "菜单ID", required = true) @PathVariable String id) {
        AdminMenu domain = adminMenuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "更新菜单")
    @PutMapping("/api/admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "新的菜单", required = true) @Valid @RequestBody AdminMenu domain) {
        log.debug("REST request to update admin menu: {}", domain);
        adminMenuRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        adminMenuRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getCode())).build();
    }

    @Operation(summary = "根据ID删除管理菜单")
    @DeleteMapping("/api/admin-menus/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "菜单ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete admin menu: {}", id);
        AdminMenu adminMenu = adminMenuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        adminMenuRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", adminMenu.getCode())).build();
    }

    @Operation(summary = "检索父类菜单")
    @GetMapping("/api/admin-menus/parents")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AdminMenu>> findParents(
            @Parameter(description = "应用名称", required = true) @RequestParam(value = "appName") String appName,
            @Parameter(description = "菜单级别", required = true) @RequestParam(value = "level") Integer level) {
        List<AdminMenu> all = adminMenuRepository.findByAppNameAndLevel(appName, level);
        return ResponseEntity.ok(all);
    }

    @Operation(summary = "根据ID提升管理菜单顺序")
    @PutMapping("/api/admin-menus/move-up/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void moveUp(@Parameter(description = "菜单ID", required = true) @PathVariable String id) {
        adminMenuService.moveUp(id);
    }

    @Operation(summary = "根据ID降低管理菜单顺序")
    @PutMapping("/api/admin-menus/move-down/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void moveDown(@Parameter(description = "菜单ID", required = true) @PathVariable String id) {
        adminMenuService.moveDown(id);
    }

    @Operation(summary = "复制管理菜单")
    @GetMapping("/api/admin-menus/copy")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void copyMenus(@Parameter(description = "源应用名称", required = true, schema = @Schema(defaultValue = "Passport")) @RequestParam(value = "sourceAppName") String sourceAppName,
                          @Parameter(description = "目标应用名称", required = true) @RequestParam(value = "targetAppName") String targetAppName) {
        List<AdminMenu> sourceMenus = adminMenuRepository.findByAppName(sourceAppName);
        sourceMenus.forEach(menu -> {
            menu.setAppName(targetAppName);
            menu.setId(null);
        });
        adminMenuRepository.saveAll(sourceMenus);
    }

    @Operation(summary = "导入管理菜单", description = "输入文件格式：每行先后appName,name,label,level,url,sequence数列，列之间使用tab分隔，行之间使用回车换行")
    @PostMapping(value = "/api/admin-menus/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public void importData(@Parameter(description = "文件", required = true) @RequestPart MultipartFile file) throws IOException {
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
