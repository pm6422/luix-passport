package com.luixtech.passport.controller;

import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.AuthorityMenu;
import com.luixtech.passport.domain.Menu;
import com.luixtech.passport.dto.AuthorityMenusDTO;
import com.luixtech.passport.repository.AuthorityMenuRepository;
import com.luixtech.passport.service.AuthorityMenuService;
import com.luixtech.passport.service.MenuService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;
import java.util.stream.Collectors;

import static com.luixtech.springbootframework.config.apidoc.SpringDocConfiguration.AUTH;


/**
 * REST controller for managing the authority menu.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AuthorityMenuController {
    private final AuthorityMenuRepository authorityMenuRepository;
    private final AuthorityMenuService    authorityMenuService;
    private final MenuService       menuService;
    private final HttpHeaderCreator httpHeaderCreator;

    @Operation(summary = "get authority menu list")
    @GetMapping("/api/authority-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<Menu>> findAuthorityMenus(
            @Parameter(description = "application id", required = true) @RequestParam(value = "appId") String appId,
            @Parameter(description = "authority name", required = true) @RequestParam(value = "authorityName") String authorityName) {
        List<Menu> results = menuService.getAuthorityMenus(appId, authorityName);
        return ResponseEntity.ok(results);
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

    @Operation(summary = "update authority menu")
    @PutMapping("/api/authority-menus")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new authority menu", required = true) @Valid @RequestBody AuthorityMenusDTO dto) {
        log.debug("REST request to update authority menus: {}", dto);
        // 删除当前权限下的所有菜单
        authorityMenuService.deleteByAuthorityName(dto.getAuthorityName());

        // 构建权限映射集合
        if (CollectionUtils.isNotEmpty(dto.getMenuIds())) {
            List<AuthorityMenu> adminAuthorityMenus = dto.getMenuIds()
                    .stream()
                    .map(adminMenuId -> new AuthorityMenu(dto.getAuthorityName(), adminMenuId))
                    .collect(Collectors.toList());
            // 批量插入
            authorityMenuRepository.saveAll(adminAuthorityMenus);
        }
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1012")).build();
    }
}