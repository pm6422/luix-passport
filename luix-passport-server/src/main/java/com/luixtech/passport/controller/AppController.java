package com.luixtech.passport.controller;

import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.passport.domain.App;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.AppRepository;
import com.luixtech.passport.service.AppService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.luixtech.springbootframework.config.apidoc.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing apps.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AppController {
    private final AppRepository     appRepository;
    private final AppService        appService;
    private final HttpHeaderCreator httpHeaderCreator;

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
        Page<App> domains = appRepository.findAll(pageable);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find application by id")
    @GetMapping("/api/apps/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<App> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        App app = appRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(app);
    }

    @Operation(summary = "update application")
    @PutMapping("/api/apps")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "application", required = true) @Valid @RequestBody App domain) {
        log.debug("REST request to update app: {}", domain);
        appService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getName())).build();
    }

    @Operation(summary = "delete application by id", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/apps/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete app: {}", id);
        appService.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
