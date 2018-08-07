package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.*;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.dto.AppAuthorityDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.service.AppAuthorityService;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * REST controller for managing the app authority.
 */
@RestController
@Api(tags = "应用权限")
public class AppAuthorityController {

    private static final Logger                 LOGGER = LoggerFactory.getLogger(AppAuthorityController.class);
    @Autowired
    private              AppAuthorityRepository appAuthorityRepository;
    @Autowired
    private              AppAuthorityService    appAuthorityService;
    @Autowired
    private              HttpHeaderCreator      httpHeaderCreator;

    @ApiOperation("创建应用权限")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典名已存在")})
    @PostMapping("/api/app-authority/app-authorities")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<Void> create(
            @ApiParam(value = "应用权限信息", required = true) @Valid @RequestBody AppAuthorityDTO dto) {
        LOGGER.debug("REST request to create app authority: {}", dto);
        appAuthorityRepository.findOneByAppNameAndAuthorityName(dto.getAppName(), dto.getAuthorityName())
                .ifPresent((existingEntity) -> {
                    throw new FieldValidationException("appAuthorityDTO", "appName+authorityName",
                            MessageFormat.format("appName: {0}, authorityName: {1}", dto.getAppName(),
                                    dto.getAuthorityName()),
                            "error.app.name.authority.name.exist",
                            MessageFormat.format("appName: {0}, authorityName: {1}", dto.getAppName(), dto));
                });

        AppAuthority appAuthority = appAuthorityRepository.save(AppAuthority.of(dto));
        return ResponseEntity
                .status(HttpStatus.CREATED).headers(httpHeaderCreator
                        .createSuccessHeader("notification.app.authority.created", appAuthority.getAuthorityName()))
                .build();
    }

    @ApiOperation("获取应用权限分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/app-authority/app-authorities")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<List<AppAuthorityDTO>> find(Pageable pageable,
                                                      @ApiParam(value = "应用名称", required = false) @RequestParam(value = "appName", required = false) String appName,
                                                      @ApiParam(value = "权限名称", required = false) @RequestParam(value = "authorityName", required = false) String authorityName)
            throws URISyntaxException {
        Page<AppAuthority> appAuthorities = appAuthorityService.findByAppNameAndAuthorityNameCombinations(pageable,
                appName, authorityName);
        List<AppAuthorityDTO> appAuthorityDTOs = appAuthorities.getContent().stream().map(entity -> entity.asDTO())
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(appAuthorities,
                "/api/app-authority/app-authorities");
        return new ResponseEntity<>(appAuthorityDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据字典ID检索应用权限信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "应用权限不存在")})
    @GetMapping("/api/app-authority/app-authorities/{id}")
    @Secured({Authority.DEVELOPER, Authority.USER})
    @Timed
    public ResponseEntity<AppAuthorityDTO> findById(
            @ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to get app authority : {}", id);
        AppAuthority entity = appAuthorityRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(entity.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("根据应用名称检索应用权限信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/app-authority/app-name/{appName}")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<List<AppAuthorityDTO>> findByApp(
            @ApiParam(value = "应用名称", required = true) @PathVariable String appName) {
        LOGGER.debug("REST request to get app authorities : {}", appName);
        List<AppAuthority> appAuthorities = appAuthorityRepository.findByAppName(appName);
        if (CollectionUtils.isEmpty(appAuthorities)) {
            return new ResponseEntity<>(Collections.emptyList(), HttpStatus.OK);
        }
        List<AppAuthorityDTO> items = appAuthorities.stream().map(item -> item.asDTO()).collect(Collectors.toList());
        return new ResponseEntity<>(items, HttpStatus.OK);
    }

    @ApiOperation("更新应用权限信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "应用权限不存在")})
    @PutMapping("/api/app-authority/app-authorities")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的应用权限信息", required = true) @Valid @RequestBody AppAuthorityDTO dto) {
        LOGGER.debug("REST request to update app authority: {}", dto);
        appAuthorityRepository.findById(dto.getId()).orElseThrow(() -> new NoDataException(dto.getId()));
        appAuthorityRepository.save(AppAuthority.of(dto));
        return ResponseEntity.status(HttpStatus.OK).headers(
                httpHeaderCreator.createSuccessHeader("notification.app.authority.updated", dto.getAuthorityName()))
                .build();
    }

    @ApiOperation(value = "根据字典ID删除应用权限信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "应用权限不存在")})
    @DeleteMapping("/api/app-authority/app-authorities/{id}")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<Void> delete(@ApiParam(value = "字典编号", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete app authority: {}", id);
        AppAuthority appAuthority = appAuthorityRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        appAuthorityRepository.deleteById(id);
        LOGGER.info("Deleted app authority");
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("notification.app.authority.deleted",
                appAuthority.getAuthorityName())).build();
    }
}
