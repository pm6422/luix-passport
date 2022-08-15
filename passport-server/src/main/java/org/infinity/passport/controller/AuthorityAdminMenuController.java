package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
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
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

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
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AuthorityAdminMenuController {
    private final AuthorityAdminMenuRepository authorityAdminMenuRepository;
    private final AdminMenuRepository          adminMenuRepository;
    private final AdminMenuService             adminMenuService;
    private final HttpHeaderCreator            httpHeaderCreator;

    @Operation(summary = "get authority menu list")
    @GetMapping("/api/authority-admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AdminMenu>> findAuthorityMenus(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName,
            @Parameter(description = "authority name", required = true) @RequestParam(value = "authorityName") String authorityName) {
        List<AdminMenu> results = adminMenuService.getAuthorityMenus(appName, authorityName);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "update authority menu")
    @PutMapping("/api/authority-admin-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new authority menu", required = true) @Valid @RequestBody AdminAuthorityMenusDTO dto) {
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

    @Operation(summary = "find menus associated with current user")
    @GetMapping("/api/authority-admin-menus/user-links")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<List<AdminMenu>> findUserAuthorityLinks(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName) {
        List<AdminMenu> results = adminMenuService.getUserAuthorityLinks(appName);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "find menu tree associated with current user")
    @GetMapping("/api/authority-admin-menus/user-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<List<AdminMenu>> findUserAuthorityMenus(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName) {
        List<AdminMenu> results = adminMenuService.getUserAuthorityMenus(appName);
        return ResponseEntity.ok(results);
    }
}
