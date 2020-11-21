package org.infinity.passport.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.io.IOException;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the admin menu.
 */
@RestController
@Api(tags = "管理菜单")
@Slf4j
public class AdminMenuController {

    private final AdminMenuRepository adminMenuRepository;
    private final AdminMenuService    adminMenuService;
    private final HttpHeaderCreator   httpHeaderCreator;

    public AdminMenuController(AdminMenuRepository adminMenuRepository, AdminMenuService adminMenuService, HttpHeaderCreator httpHeaderCreator) {
        this.adminMenuRepository = adminMenuRepository;
        this.adminMenuService = adminMenuService;
        this.httpHeaderCreator = httpHeaderCreator;
    }

    @ApiOperation("创建菜单")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建")})
    @PostMapping("/api/admin-menu/menus")
    @Secured({Authority.ADMIN})
    public ResponseEntity<Void> create(
            @ApiParam(value = "菜单信息", required = true) @Valid @RequestBody AdminMenuDTO dto) {
        log.debug("REST request to create admin menu: {}", dto);
        adminMenuRepository.findOneByAppNameAndLevelAndSequence(dto.getAppName(), dto.getLevel(), dto.getSequence())
                .ifPresent((existingEntity) -> {
                    throw new FieldValidationException("adminMenuDTO", "appName+sequence",
                            MessageFormat.format("appName: {0}, sequence: {1}", dto.getAppName(), dto.getSequence()),
                            "error.duplication",
                            MessageFormat.format("appName: {0}, sequence: {1}", dto.getAppName(), dto.getSequence()));
                });
        adminMenuRepository.save(AdminMenu.of(dto));
        return ResponseEntity.status(HttpStatus.CREATED).headers(
                httpHeaderCreator.createSuccessHeader("notification.admin.menu.created", dto.getName()))
                .build();
    }

    @ApiOperation("获取菜单信息分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/admin-menu/menus")
    @Secured({Authority.ADMIN})
    public ResponseEntity<List<AdminMenuDTO>> find(Pageable pageable,
                                                   @ApiParam(value = "应用名称") @RequestParam(value = "appName", required = false) String appName)
            throws URISyntaxException {
        Page<AdminMenu> adminMenus = StringUtils.isEmpty(appName) ? adminMenuRepository.findAll(pageable)
                : adminMenuRepository.findByAppName(pageable, appName);
        List<AdminMenuDTO> DTOs = adminMenus.getContent().stream().map(AdminMenu::asDTO)
                .collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(adminMenus, "/api/admin-menu/menus");
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据菜单ID查询菜单")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "菜单不存在")})
    @GetMapping("/api/admin-menu/menus/{id}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<AdminMenuDTO> findById(@ApiParam(value = "菜单ID", required = true) @PathVariable String id) {
        AdminMenu entity = adminMenuRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        return ResponseEntity.ok(entity.asDTO());
    }

    @ApiOperation("查询父类菜单")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/admin-menu/parent-menus/{appName}/{level:[0-9]+}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<List<AdminMenuDTO>> findAllParentMenu(
            @ApiParam(value = "应用名称", required = true) @PathVariable String appName,
            @ApiParam(value = "菜单级别", required = true) @PathVariable Integer level) {
        List<AdminMenuDTO> dtos = adminMenuRepository.findByAppNameAndLevel(appName, level).stream()
                .map(AdminMenu::asDTO).collect(Collectors.toList());
        return ResponseEntity.ok(dtos);
    }

    @ApiOperation("更新菜单")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "菜单不存在")})
    @PutMapping("/api/admin-menu/menus")
    @Secured({Authority.ADMIN})
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的菜单信息", required = true) @Valid @RequestBody AdminMenuDTO dto) {
        log.debug("REST request to update admin menu: {}", dto);
        adminMenuRepository.findById(dto.getId()).orElseThrow(() -> new NoDataException(dto.getId()));

        adminMenuRepository.save(AdminMenu.of(dto));
        return ResponseEntity.ok().headers(
                httpHeaderCreator.createSuccessHeader("notification.admin.menu.updated", dto.getName()))
                .build();
    }

    @ApiOperation("根据应用名称和菜单ID删除管理菜单")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "菜单不存在")})
    @DeleteMapping("/api/admin-menu/menus/{id}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<Void> delete(@ApiParam(value = "菜单ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete admin menu: {}", id);
        AdminMenu adminMenu = adminMenuRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        adminMenuRepository.deleteById(id);
        return ResponseEntity.ok().headers(
                httpHeaderCreator.createSuccessHeader("notification.admin.menu.deleted", adminMenu.getName()))
                .build();
    }

    @ApiOperation(value = "导入管理菜单", notes = "输入文件格式：每行先后appName,name,label,level,url,sequence数列，列之间使用tab分隔，行之间使用回车换行")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功导入")})
    @PostMapping(value = "/api/admin-menu/menus/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    @Secured({Authority.ADMIN})
    public void importData(@ApiParam(value = "文件", required = true) @RequestPart MultipartFile file)
            throws IOException {
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

    @ApiOperation("根据菜单ID提高管理菜单顺序")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功操作")})
    @GetMapping("/api/admin-menu/raise-seq/{id}")
    @Secured({Authority.ADMIN})
    public void raiseSeq(@ApiParam(value = "菜单ID", required = true) @PathVariable String id) {
        adminMenuService.raiseSeq(id);
    }

    @ApiOperation("根据菜单ID降低管理菜单顺序")
    @ApiResponses(value = {@ApiResponse(code = 200, message = "成功操作")})
    @GetMapping("/api/admin-menu/lower-seq/{id}")
    @Secured({Authority.ADMIN})
    public void lowerSeq(@ApiParam(value = "菜单ID", required = true) @PathVariable String id) {
        adminMenuService.lowerSeq(id);
    }

    @ApiOperation("复制管理菜单")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功复制")})
    @GetMapping("/api/admin-menu/copy-menus")
    @Secured({Authority.ADMIN})
    public void copyMenus(@ApiParam(value = "源应用名称", required = true, defaultValue = "DeepBrainPassport") @RequestParam(value = "sourceAppName") String sourceAppName,
                          @ApiParam(value = "目标应用名称", required = true) @RequestParam(value = "targetAppName") String targetAppName) {
        List<AdminMenu> sourceMenus = adminMenuRepository.findByAppName(sourceAppName);
        sourceMenus.forEach(menu -> {
            menu.setAppName(targetAppName);
            menu.setId(null);
        });
        adminMenuRepository.saveAll(sourceMenus);
    }
}
