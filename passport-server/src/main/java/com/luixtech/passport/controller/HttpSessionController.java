package com.luixtech.passport.controller;

import com.luixtech.framework.component.HttpHeaderCreator;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.HttpSession;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.HttpSessionRepository;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

import static com.luixtech.framework.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing http sessions.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class HttpSessionController {
    private final HttpSessionRepository httpSessionRepository;
    private final HttpHeaderCreator     httpHeaderCreator;

    @Operation(summary = "find http session list")
    @GetMapping("/api/http-sessions")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<List<HttpSession>> find(@ParameterObject Pageable pageable,
                                                  @Parameter(description = "user name") @RequestParam(value = "principal", required = false) String principal) {
        Page<HttpSession> sessions = StringUtils.isEmpty(principal) ? httpSessionRepository.findAll(pageable) : httpSessionRepository.findByPrincipal(pageable, principal);
        HttpHeaders headers = generatePageHeaders(sessions);
        return ResponseEntity.ok().headers(headers).body(sessions.getContent());
    }

    @Operation(summary = "delete http session by ID", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/http-sessions/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.DEVELOPER + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete http session: {}", id);
        httpSessionRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        httpSessionRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
