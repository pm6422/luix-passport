package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2RefreshToken;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.OAuth2RefreshTokenRepository;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
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

@RestController
@Tag(name = "刷新令牌信息")
@SecurityRequirement(name = AUTH)
@Slf4j
public class OAuth2RefreshTokenController {

    @Resource
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;
    @Resource
    private HttpHeaderCreator            httpHeaderCreator;

    @Operation(summary = "分页检索刷新令牌列表")
    @GetMapping("/api/oauth2-refresh-tokens")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<MongoOAuth2RefreshToken>> find(@ParameterObject Pageable pageable,
                                                              @Parameter(description = "刷新令牌ID") @RequestParam(value = "tokenId", required = false) String tokenId,
                                                              @Parameter(description = "客户端ID") @RequestParam(value = "clientId", required = false) String clientId,
                                                              @Parameter(description = "用户名") @RequestParam(value = "userName", required = false) String userName) {
        MongoOAuth2RefreshToken probe = new MongoOAuth2RefreshToken();
        probe.setId(tokenId);
        probe.setClientId(clientId);
        probe.setUserName(userName);
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Page<MongoOAuth2RefreshToken> tokens = oAuth2RefreshTokenRepository.findAll(Example.of(probe, matcher), pageable);
        HttpHeaders headers = generatePageHeaders(tokens);
        return ResponseEntity.ok().headers(headers).body(tokens.getContent());
    }

    @Operation(summary = "根据ID检索刷新令牌")
    @GetMapping("/api/oauth2-refresh-tokens/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<MongoOAuth2RefreshToken> findById(
            @Parameter(description = "刷新令牌ID", required = true) @PathVariable String id) {
        MongoOAuth2RefreshToken domain = oAuth2RefreshTokenRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "根据ID删除刷新令牌", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/oauth2-refresh-tokens/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "刷新令牌ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth2 access token: {}", id);
        oAuth2RefreshTokenRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oAuth2RefreshTokenRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", id))
                .build();
    }
}
