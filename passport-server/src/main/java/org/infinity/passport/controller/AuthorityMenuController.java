package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.AuthorityMenu;
import org.infinity.passport.domain.Menu;
import org.infinity.passport.dto.AuthorityMenusDTO;
import org.infinity.passport.repository.AuthorityMenuRepository;
import org.infinity.passport.repository.MenuRepository;
import org.infinity.passport.service.MenuService;
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
 * REST controller for managing the authority menu.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AuthorityMenuController {
    private final AuthorityMenuRepository authorityMenuRepository;
    private final MenuRepository          menuRepository;
    private final MenuService             menuService;
    private final HttpHeaderCreator       httpHeaderCreator;

    @Operation(summary = "get authority menu list")
    @GetMapping("/api/authority-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<Menu>> findAuthorityMenus(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName,
            @Parameter(description = "authority name", required = true) @RequestParam(value = "authorityName") String authorityName) {
        List<Menu> results = menuService.getAuthorityMenus(appName, authorityName);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "update authority menu")
    @PutMapping("/api/authority-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new authority menu", required = true) @Valid @RequestBody AuthorityMenusDTO dto) {
        log.debug("REST request to update authority menus: {}", dto);
        // 删除当前权限下的所有菜单
        Set<String> appAdminMenuIds = menuRepository.findByAppId(dto.getAppName()).stream().map(Menu::getId)
                .collect(Collectors.toSet());
        authorityMenuRepository.deleteByAuthorityNameAndMenuIdIn(dto.getAuthorityName(),
                new ArrayList<>(appAdminMenuIds));

        // 构建权限映射集合
        if (CollectionUtils.isNotEmpty(dto.getMenuIds())) {
            List<AuthorityMenu> adminAuthorityMenus = dto.getMenuIds().stream()
                    .map(adminMenuId -> new AuthorityMenu(dto.getAuthorityName(), adminMenuId))
                    .collect(Collectors.toList());
            // 批量插入
            authorityMenuRepository.saveAll(adminAuthorityMenus);
        }
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1012")).build();
    }

    @Operation(summary = "find menus associated with current user")
    @GetMapping("/api/authority-menus/user-links")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<List<Menu>> findUserAuthorityLinks(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName) {
        List<Menu> results = menuService.getUserAuthorityLinks(appName);
        return ResponseEntity.ok(results);
    }

    @Operation(summary = "find menu tree associated with current user")
    @GetMapping("/api/authority-menus/user-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<List<Menu>> findUserAuthorityMenus(
            @Parameter(description = "application name", required = true) @RequestParam(value = "appName") String appName) {
        List<Menu> results = menuService.getUserAuthorityMenus(appName);
        return ResponseEntity.ok(results);
    }
}
