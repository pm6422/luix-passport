package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2AuthorizationCode;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.OAuth2AuthorizationCodeRepository;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Tag(name = "登录授权码")
@Slf4j
public class OAuth2AuthorizationCodeController {

    @Resource
    private OAuth2AuthorizationCodeRepository oAuth2AuthorizationCodeRepository;
    @Resource
    private HttpHeaderCreator                 httpHeaderCreator;

    /**
     * Authorization code will be deleted immediately after authentication process.
     * So the database will be always empty.
     *
     * @param pageable            pagination info
     * @param authorizationCodeId authorization code id
     * @param code                authorization code
     * @return code list
     */
    @Operation(summary = "分页检索授权码列表")
    @GetMapping("/api/oauth2-authorization-codes")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<MongoOAuth2AuthorizationCode>> find(@ParameterObject Pageable pageable,
                                                                   @Parameter(description = "授权码ID") @RequestParam(value = "authorizationCodeId", required = false) String authorizationCodeId,
                                                                   @Parameter(description = "授权码") @RequestParam(value = "code", required = false) String code) {
        MongoOAuth2AuthorizationCode probe = new MongoOAuth2AuthorizationCode();
        probe.setId(authorizationCodeId);
        probe.setCode(code);
        Page<MongoOAuth2AuthorizationCode> codes = oAuth2AuthorizationCodeRepository.findAll(Example.of(probe), pageable);
        HttpHeaders headers = generatePageHeaders(codes);
        return ResponseEntity.ok().headers(headers).body(codes.getContent());
    }

    @Operation(summary = "根据ID检索授权码")
    @GetMapping("/api/oauth2-authorization-codes/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<MongoOAuth2AuthorizationCode> findById(
            @Parameter(description = "授权码ID", required = true) @PathVariable String id) {
        MongoOAuth2AuthorizationCode domain = oAuth2AuthorizationCodeRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "根据ID删除授权码", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/oauth2-authorization-codes/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "授权码ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth2 authorization code: {}", id);
        oAuth2AuthorizationCodeRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oAuth2AuthorizationCodeRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", id))
                .build();
    }
}
