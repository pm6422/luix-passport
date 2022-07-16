package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.AuthorityAdminMenu;
import org.infinity.passport.dto.AdminAuthorityMenusDTO;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.repository.AuthorityAdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;

/**
 * REST controller for managing the authority admin menu.
 */
@RestController
@Tag(name = "权限管理菜单")
@SecurityRequirement(name = AUTH)
@Slf4j
public class AuthorityAdminMenuController {

    @Resource
    private AuthorityAdminMenuRepository authorityAdminMenuRepository;
    @Resource
    private AdminMenuRepository          adminMenuRepository;
    @Resource
    private AdminMenuService             adminMenuService;
    @Resource
    private HttpHeaderCreator            httpHeaderCreator;

    @Operation(summary = "根据权限名称检索菜单树")
    @GetMapping("/api/authority-admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AdminMenu>> findAuthorityMenus(
            @Parameter(description = "应用名称", required = true) @RequestParam(value = "appName") String appName,
            @Parameter(description = "权限名称", required = true) @RequestParam(value = "authorityName") String authorityName) {
        List<AdminMenu> results = adminMenuService.getAuthorityMenus(appName, authorityName);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "更新权限菜单")
    @PutMapping("/api/authority-admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "新的权限菜单信息", required = true) @Valid @RequestBody AdminAuthorityMenusDTO dto) {
        log.debug("REST request to update admin authority menus: {}", dto);
        // 删除当前权限下的所有菜单
        Set<String> appAdminMenuIds = adminMenuRepository.findByAppName(dto.getAppName()).stream().map(AdminMenu::getId)
                .collect(Collectors.toSet());
        authorityAdminMenuRepository.deleteByAuthorityNameAndAdminMenuIdIn(dto.getAuthorityName(),
                new ArrayList<>(appAdminMenuIds));

        // 构建权限映射集合
        if (CollectionUtils.isNotEmpty(dto.getAdminMenuIds())) {
            List<AuthorityAdminMenu> adminAuthorityMenus = dto.getAdminMenuIds().stream()
                    .map(adminMenuId -> new AuthorityAdminMenu(dto.getAuthorityName(), adminMenuId))
                    .collect(Collectors.toList());
            // 批量插入
            authorityAdminMenuRepository.saveAll(adminAuthorityMenus);
        }
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1012")).build();
    }

    @Operation(summary = "检索当前用户权限关联的菜单列表")
    @GetMapping("/api/authority-admin-menus/user-links")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<List<AdminMenu>> findUserAuthorityLinks(
            @Parameter(description = "应用名称", required = true) @RequestParam(value = "appName") String appName) {
        List<AdminMenu> results = adminMenuService.getUserAuthorityLinks(appName);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "检索当前用户权限关联的菜单树")
    @GetMapping("/api/authority-admin-menus/user-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<List<AdminMenu>> findUserAuthorityMenus(
            @Parameter(description = "应用名称", required = true) @RequestParam(value = "appName") String appName) {
        List<AdminMenu> results = adminMenuService.getUserAuthorityMenus(appName);
        return ResponseEntity.ok(results);
    }
}
