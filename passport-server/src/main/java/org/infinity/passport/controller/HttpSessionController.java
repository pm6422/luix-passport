package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.HttpSession;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.HttpSessionRepository;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing http sessions.
 */
@RestController
@Tag(name = "Http会话")
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class HttpSessionController {
    private final HttpSessionRepository httpSessionRepository;
    private final HttpHeaderCreator     httpHeaderCreator;

    @Operation(summary = "分页检索Http会话列表")
    @GetMapping("/api/http-sessions")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<List<HttpSession>> find(@ParameterObject Pageable pageable,
                                                  @Parameter(description = "用户名称") @RequestParam(value = "principal", required = false) String principal) {
        Page<HttpSession> sessions = StringUtils.isEmpty(principal) ? httpSessionRepository.findAll(pageable) : httpSessionRepository.findByPrincipal(pageable, principal);
        HttpHeaders headers = generatePageHeaders(sessions);
        return ResponseEntity.ok().headers(headers).body(sessions.getContent());
    }

    @Operation(summary = "根据ID删除Http会话", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/http-sessions/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "Http会话ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete http session: {}", id);
        httpSessionRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        httpSessionRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
