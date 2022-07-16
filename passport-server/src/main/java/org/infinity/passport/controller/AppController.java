package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.App;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.repository.AppRepository;
import org.infinity.passport.service.AppService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing apps.
 */
@RestController
@Tag(name = "应用管理")
@Slf4j
public class AppController {

    @Resource
    private AppRepository          appRepository;
    @Resource
    private AppAuthorityRepository appAuthorityRepository;
    @Resource
    private AppService             appService;
    @Resource
    private HttpHeaderCreator      httpHeaderCreator;

    @Operation(summary = "创建应用")
    @PostMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(@Parameter(description = "应用", required = true) @Valid @RequestBody App domain) {
        log.debug("REST request to create app: {}", domain);
        appService.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getName())).build();
    }

    @Operation(summary = "分页检索应用列表")
    @GetMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<App>> find(@ParameterObject Pageable pageable) {
        Page<App> apps = appRepository.findAll(pageable);
        return ResponseEntity.ok().headers(generatePageHeaders(apps)).body(apps.getContent());
    }

    @Operation(summary = "根据名称检索应用")
    @GetMapping("/api/apps/{name}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<App> findById(@Parameter(description = "应用名称", required = true) @PathVariable String name) {
        App app = appRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        List<AppAuthority> appAuthorities = appAuthorityRepository.findByAppName(name);
        Set<String> authorities = appAuthorities.stream().map(AppAuthority::getAuthorityName).collect(Collectors.toSet());
        app.setAuthorities(authorities);
        return ResponseEntity.ok(app);
    }

    @Operation(summary = "更新应用")
    @PutMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "新的应用", required = true) @Valid @RequestBody App domain) {
        log.debug("REST request to update app: {}", domain);
        appService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getName())).build();
    }

    @Operation(summary = "根据名称删除应用", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/apps/{name}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "应用名称", required = true) @PathVariable String name) {
        log.debug("REST request to delete app: {}", name);
        appRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        appRepository.deleteById(name);
        appAuthorityRepository.deleteByAppName(name);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", name)).build();
    }
}
