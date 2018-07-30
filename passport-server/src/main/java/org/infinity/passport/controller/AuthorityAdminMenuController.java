package org.infinity.passport.controller;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.AuthorityAdminMenu;
import org.infinity.passport.dto.AdminAuthorityMenusDTO;
import org.infinity.passport.dto.AdminManagedMenuDTO;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.repository.AuthorityAdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.service.AuthorityService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.SecurityUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

/**
 * REST controller for managing the authority admin menu.
 */
@RestController
@Api(tags = "权限管理菜单")
public class AuthorityAdminMenuController {

    private static final Logger          LOGGER = LoggerFactory.getLogger(AuthorityAdminMenuController.class);

    @Autowired
    private AuthorityAdminMenuRepository authorityAdminMenuRepository;

    @Autowired
    private AdminMenuRepository          adminMenuRepository;

    @Autowired
    private AdminMenuService             adminMenuService;

    @Autowired
    private AuthorityService             authorityService;

    @Autowired
    private HttpHeaderCreator            httpHeaderCreator;

    @ApiOperation("查询当前用户权限关联的菜单")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取") })
    @GetMapping("/api/authority-admin-menu/authority-menus")
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<List<AdminManagedMenuDTO>> getAuthorityMenus(
            @ApiParam(value = "应用名称", required = true) @RequestParam(value = "appName", required = true) String appName) {
        List<String> allEnabledAuthorities = authorityService.findAllAuthorityNames(true);
        List<String> userEnabledAuthorities = SecurityUtils.getCurrentUserRoles().parallelStream()
                .filter(userAuthority -> allEnabledAuthorities.contains(userAuthority.getAuthority()))
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        List<AdminManagedMenuDTO> results = adminMenuService.getAuthorityMenus(appName, userEnabledAuthorities);
        return new ResponseEntity<List<AdminManagedMenuDTO>>(results, HttpStatus.OK);
    }

    @ApiOperation("查询当前用户权限关联的链接")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取") })
    @GetMapping("/api/authority-admin-menu/authority-links")
    @Secured({ Authority.USER })
    @Timed
    public ResponseEntity<List<AdminMenuDTO>> getAuthorityLinks(
            @ApiParam(value = "应用名称", required = true) @RequestParam(value = "appName", required = true) String appName) {
        List<String> allEnabledAuthorities = authorityService.findAllAuthorityNames(true);
        List<String> userEnabledAuthorities = SecurityUtils.getCurrentUserRoles().parallelStream()
                .filter(userAuthority -> allEnabledAuthorities.contains(userAuthority.getAuthority()))
                .map(GrantedAuthority::getAuthority).collect(Collectors.toList());

        List<AdminMenuDTO> results = adminMenuService.getAuthorityLinks(appName, userEnabledAuthorities);
        return new ResponseEntity<List<AdminMenuDTO>>(results, HttpStatus.OK);
    }

    @ApiOperation("根据权限名称查看菜单信息")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取") })
    @GetMapping("/api/authority-admin-menu/menu-info")
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<List<AdminManagedMenuDTO>> getMenus(
            @ApiParam(value = "应用名称", required = true) @RequestParam(value = "appName", required = true) String appName,
            @ApiParam(value = "权限名称", required = true) @RequestParam(value = "authorityName", required = true) String authorityName) {
        // 查询全部管理菜单
        List<AdminMenu> allAdminMenus = adminMenuRepository.findByAppName(appName);
        if (CollectionUtils.isEmpty(allAdminMenus)) {
            return new ResponseEntity<List<AdminManagedMenuDTO>>(new ArrayList<AdminManagedMenuDTO>(), HttpStatus.OK);
        }

        // 查询当前权限拥有的管理菜单
        Set<String> adminMenuIds = authorityAdminMenuRepository.findByAuthorityName(authorityName).stream()
                .map(AuthorityAdminMenu::getAdminMenuId).collect(Collectors.toSet());

        // 转换成展示菜单
        List<AdminMenuDTO> allAdminMenusDTO = allAdminMenus.stream().map(adminMenu -> {
            AdminMenuDTO adminMenuDTO = adminMenu.asDTO();
            if (adminMenuIds.contains(adminMenu.getId())) {
                adminMenuDTO.setChecked(true);
            }
            return adminMenuDTO;
        }).collect(Collectors.toList());

        List<AdminManagedMenuDTO> results = adminMenuService.classifyAdminMenu(allAdminMenusDTO);
        return new ResponseEntity<List<AdminManagedMenuDTO>>(results, HttpStatus.OK);
    }

    @ApiOperation("更新权限菜单")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功更新"), @ApiResponse(code = SC_BAD_REQUEST, message = "权限信息不存在") })
    @PutMapping("/api/authority-admin-menu/update-authority-menus")
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的权限菜单信息", required = true) @Valid @RequestBody AdminAuthorityMenusDTO dto) {
        LOGGER.debug("REST request to update admin authority menus: {}", dto);
        // 删除当前权限下的所有菜单
        Set<String> appAdminMenuIds = adminMenuRepository.findByAppName(dto.getAppName()).stream().map(AdminMenu::getId)
                .collect(Collectors.toSet());
        authorityAdminMenuRepository.deleteByAuthorityNameAndAdminMenuIdIn(dto.getAuthorityName(),
                new ArrayList<String>(appAdminMenuIds));

        // 构建权限映射集合
        if (CollectionUtils.isNotEmpty(dto.getAdminMenuIds())) {
            List<AuthorityAdminMenu> adminAuthorityMenus = dto.getAdminMenuIds().stream()
                    .map(adminMenuId -> new AuthorityAdminMenu(dto.getAuthorityName(), adminMenuId))
                    .collect(Collectors.toList());
            // 批量插入
            authorityAdminMenuRepository.saveAll(adminAuthorityMenus);
        }
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.admin.authority.menu.updated")).build();
    }
}
