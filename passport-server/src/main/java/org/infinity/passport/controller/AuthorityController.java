package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.repository.AuthorityRepository;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing authorities.
 */
@RestController
@Tag(name = "权限管理")
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AuthorityController {
    private final AuthorityRepository authorityRepository;
    private final HttpHeaderCreator   httpHeaderCreator;

    @Operation(summary = "创建权限")
    @PostMapping("/api/authorities")
    public ResponseEntity<Void> create(
            @Parameter(description = "权限", required = true) @Valid @RequestBody Authority domain) {
        log.debug("REST request to create authority: {}", domain);
        authorityRepository.findById(domain.getName()).ifPresent(app -> {
            throw new DuplicationException(ImmutableMap.of("name", domain.getName()));
        });
        authorityRepository.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getName()))
                .build();
    }

    @Operation(summary = "分页检索权限列表")
    @GetMapping("/api/authorities")
    public ResponseEntity<List<Authority>> find(@ParameterObject Pageable pageable) {
        Page<Authority> authorities = authorityRepository.findAll(pageable);
        HttpHeaders headers = generatePageHeaders(authorities);
        return ResponseEntity.ok().headers(headers).body(authorities.getContent());
    }

    @Operation(summary = "根据权限名称检索权限")
    @GetMapping("/api/authorities/{name}")
    public ResponseEntity<Authority> findById(
            @Parameter(description = "权限名称", required = true) @PathVariable String name) {
        Authority domain = authorityRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "更新权限")
    @PutMapping("/api/authorities")
    public ResponseEntity<Void> update(
            @Parameter(description = "新的权限", required = true) @Valid @RequestBody Authority domain) {
        log.debug("REST request to update authority: {}", domain);
        authorityRepository.findById(domain.getName()).orElseThrow(() -> new DataNotFoundException(domain.getName()));
        authorityRepository.save(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getName())).build();
    }

    @Operation(summary = "根据名称删除权限", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/authorities/{name}")
    public ResponseEntity<Void> delete(@Parameter(description = "权限名称", required = true) @PathVariable String name) {
        log.debug("REST request to delete authority: {}", name);
        authorityRepository.findById(name).orElseThrow(() -> new DataNotFoundException(name));
        authorityRepository.deleteById(name);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", name)).build();
    }
}
