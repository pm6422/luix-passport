package com.luixtech.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.luixtech.passport.component.HttpHeaderCreator;
import com.luixtech.passport.domain.AppAuthority;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.exception.DuplicationException;
import com.luixtech.passport.repository.AppAuthorityRepository;
import com.luixtech.passport.service.AppAuthorityService;
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

import static com.luixtech.passport.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing the app authority.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AppAuthorityController {
    private final AppAuthorityRepository appAuthorityRepository;
    private final AppAuthorityService    appAuthorityService;
    private final HttpHeaderCreator      httpHeaderCreator;

    @Operation(summary = "create application authority")
    @PostMapping("/api/app-authorities")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "application authority", required = true) @Valid @RequestBody AppAuthority domain) {
        log.debug("REST request to create app authority: {}", domain);
        appAuthorityRepository.findOneByAppIdAndAuthorityName(domain.getAppId(), domain.getAuthorityName())
                .ifPresent((existingEntity) -> {
                    throw new DuplicationException(ImmutableMap.of("appName", domain.getAppId(), "authorityName", domain.getAuthorityName()));
                });

        AppAuthority appAuthority = appAuthorityRepository.save(domain);
        return ResponseEntity
                .status(HttpStatus.CREATED).headers(httpHeaderCreator.createSuccessHeader("SM1001", appAuthority.getAuthorityName()))
                .build();
    }

    @Operation(summary = "find application authority list")
    @GetMapping("/api/app-authorities")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<AppAuthority>> find(@ParameterObject Pageable pageable,
                                                   @Parameter(description = "application name") @RequestParam(value = "appName", required = false) String appName,
                                                   @Parameter(description = "authority name") @RequestParam(value = "authorityName", required = false) String authorityName) {
        Page<AppAuthority> appAuthorities = appAuthorityService.find(pageable, appName, authorityName);
        HttpHeaders headers = generatePageHeaders(appAuthorities);
        return ResponseEntity.ok().headers(headers).body(appAuthorities.getContent());
    }

    @Operation(summary = "find application authority by ID")
    @GetMapping("/api/app-authorities/{id}")
    @PreAuthorize("hasAnyAuthority(\"" + Authority.DEVELOPER + "\", \"" + Authority.USER + "\")")
    public ResponseEntity<AppAuthority> findById(
            @Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to get app authority : {}", id);
        AppAuthority domain = appAuthorityRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update application authority")
    @PutMapping("/api/app-authorities")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "new application authority", required = true) @Valid @RequestBody AppAuthority domain) {
        log.debug("REST request to update app authority: {}", domain);
        appAuthorityRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        appAuthorityRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getAuthorityName())).build();
    }

    @Operation(summary = "delete applcation authority by ID", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/app-authorities/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete app authority: {}", id);
        AppAuthority appAuthority = appAuthorityRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        appAuthorityRepository.deleteById(id);
        log.info("Deleted app authority");
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", appAuthority.getAuthorityName())).build();
    }
}
