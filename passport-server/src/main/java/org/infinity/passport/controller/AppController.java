package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
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

import javax.validation.Valid;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing apps.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AppController {
    private final AppRepository          appRepository;
    private final AppAuthorityRepository appAuthorityRepository;
    private final AppService             appService;
    private final HttpHeaderCreator      httpHeaderCreator;

    @Operation(summary = "create application")
    @PostMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(@Parameter(description = "application", required = true) @Valid @RequestBody App domain) {
        log.debug("REST request to create app: {}", domain);
        appService.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getName())).build();
    }

    @Operation(summary = "find application list")
    @GetMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<App>> find(@ParameterObject Pageable pageable) {
        Page<App> apps = appRepository.findAll(pageable);
        return ResponseEntity.ok().headers(generatePageHeaders(apps)).body(apps.getContent());
    }

    @Operation(summary = "find application by name")
    @GetMapping("/api/apps/{name}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<App> findById(@Parameter(description = "application name", required = true) @PathVariable String name) {
        App app = appRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        List<AppAuthority> appAuthorities = appAuthorityRepository.findByAppName(name);
        Set<String> authorities = appAuthorities.stream().map(AppAuthority::getAuthorityName).collect(Collectors.toSet());
        app.setAuthorities(authorities);
        return ResponseEntity.ok(app);
    }

    @Operation(summary = "update application")
    @PutMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "new application", required = true) @Valid @RequestBody App domain) {
        log.debug("REST request to update app: {}", domain);
        appService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getName())).build();
    }

    @Operation(summary = "delete application by name", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/apps/{name}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "application name", required = true) @PathVariable String name) {
        log.debug("REST request to delete app: {}", name);
        appRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        appRepository.deleteById(name);
        appAuthorityRepository.deleteByAppName(name);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", name)).build();
    }
}
