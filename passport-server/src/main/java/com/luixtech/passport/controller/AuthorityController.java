package com.luixtech.passport.controller;

import com.google.common.collect.ImmutableMap;
import com.luixtech.passport.component.HttpHeaderCreator;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.exception.DuplicationException;
import com.luixtech.passport.repository.AuthorityRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.luixtech.passport.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing authorities.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AuthorityController {
    private final AuthorityRepository authorityRepository;
    private final HttpHeaderCreator   httpHeaderCreator;

    @Operation(summary = "create authority")
    @PostMapping("/api/authorities")
    public ResponseEntity<Void> create(
            @Parameter(description = "authority", required = true) @Valid @RequestBody Authority domain) {
        log.debug("REST request to create authority: {}", domain);
        if (authorityRepository.countByName(domain.getName()) > 0) {
            throw new DuplicationException(ImmutableMap.of("name", domain.getName()));
        }
        authorityRepository.save(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getName()))
                .build();
    }

    @Operation(summary = "find authority list")
    @GetMapping("/api/authorities")
    public ResponseEntity<List<Authority>> find(@ParameterObject Pageable pageable) {
        Page<Authority> domains = authorityRepository.findAll(pageable);
        HttpHeaders headers = generatePageHeaders(domains);
        return ResponseEntity.ok().headers(headers).body(domains.getContent());
    }

    @Operation(summary = "find authority by name")
    @GetMapping("/api/authorities/{name}")
    public ResponseEntity<Authority> findById(
            @Parameter(description = "name", required = true) @PathVariable String name) {
        Authority domain = authorityRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update authority")
    @PutMapping("/api/authorities")
    public ResponseEntity<Void> update(
            @Parameter(description = "new authority", required = true) @Valid @RequestBody Authority domain) {
        log.debug("REST request to update authority: {}", domain);
        authorityRepository.findById(domain.getName()).orElseThrow(() -> new DataNotFoundException(domain.getName()));
        authorityRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getName())).build();
    }

    @Operation(summary = "delete authority by name", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/authorities/{name}")
    public ResponseEntity<Void> delete(@Parameter(description = "name", required = true) @PathVariable String name) {
        log.debug("REST request to delete authority: {}", name);
        authorityRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        authorityRepository.deleteById(name);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", name)).build();
    }
}
