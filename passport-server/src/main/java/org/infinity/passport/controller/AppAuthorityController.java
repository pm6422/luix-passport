package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.service.AppAuthorityService;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the app authority.
 */
@RestController
@Tag(name = "应用权限")
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AppAuthorityController {
    private final AppAuthorityRepository appAuthorityRepository;
    private final AppAuthorityService    appAuthorityService;
    private final HttpHeaderCreator      httpHeaderCreator;

    @Operation(summary = "创建应用权限")
    @PostMapping("/api/app-authorities")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "应用权限", required = true) @Valid @RequestBody AppAuthority domain) {
        log.debug("REST request to create app authority: {}", domain);
        appAuthorityRepository.findOneByAppNameAndAuthorityName(domain.getAppName(), domain.getAuthorityName())
                .ifPresent((existingEntity) -> {
                    throw new DuplicationException(ImmutableMap.of("appName", domain.getAppName(), "authorityName", domain.getAuthorityName()));
                });

        AppAuthority appAuthority = appAuthorityRepository.insert(domain);
        return ResponseEntity
                .status(HttpStatus.CREATED).headers(httpHeaderCreator.createSuccessHeader("SM1001", appAuthority.getAuthorityName()))
                .build();
    }

    @Operation(summary = "分页检索应用权限列表")
    @GetMapping("/api/app-authorities")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AppAuthority>> find(@ParameterObject Pageable pageable,
                                                   @Parameter(description = "应用名称") @RequestParam(value = "appName", required = false) String appName,
                                                   @Parameter(description = "权限名称") @RequestParam(value = "authorityName", required = false) String authorityName) {
        Page<AppAuthority> appAuthorities = appAuthorityService.find(pageable, appName, authorityName);
        HttpHeaders headers = generatePageHeaders(appAuthorities);
        return ResponseEntity.ok().headers(headers).body(appAuthorities.getContent());
    }

    @Operation(summary = "根据ID检索应用权限")
    @GetMapping("/api/app-authorities/{id}")
    @PreAuthorize("hasAnyAuthority(\"" + Authority.DEVELOPER + "\", \"" + Authority.USER + "\")")
    public ResponseEntity<AppAuthority> findById(
            @Parameter(description = "字典编号", required = true) @PathVariable String id) {
        log.debug("REST request to get app authority : {}", id);
        AppAuthority domain = appAuthorityRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "更新应用权限")
    @PutMapping("/api/app-authorities")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "新的应用权限", required = true) @Valid @RequestBody AppAuthority domain) {
        log.debug("REST request to update app authority: {}", domain);
        appAuthorityRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        appAuthorityRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getAuthorityName())).build();
    }

    @Operation(summary = "根据ID删除应用权限", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/app-authorities/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "字典编号", required = true) @PathVariable String id) {
        log.debug("REST request to delete app authority: {}", id);
        AppAuthority appAuthority = appAuthorityRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        appAuthorityRepository.deleteById(id);
        log.info("Deleted app authority");
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", appAuthority.getAuthorityName())).build();
    }
}
