package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.validation.Valid;
import java.util.List;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Tag(name = "单点登录客户端")
@Slf4j
public class OAuth2ClientDetailsController {

    @Resource
    private OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;
    @Resource
    private MongoTemplate                 mongoTemplate;
    @Resource
    private HttpHeaderCreator             httpHeaderCreator;
    @Resource
    private PasswordEncoder               passwordEncoder;

    public OAuth2ClientDetailsController(OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository,
                                         MongoTemplate mongoTemplate, HttpHeaderCreator httpHeaderCreator,
                                         PasswordEncoder passwordEncoder) {
        this.oAuth2ClientDetailsRepository = oAuth2ClientDetailsRepository;
        this.mongoTemplate = mongoTemplate;
        this.httpHeaderCreator = httpHeaderCreator;
        this.passwordEncoder = passwordEncoder;
    }

    @Operation(summary = "创建单点登录客户端")
    @PostMapping("/api/oauth2-clients")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(
            @Parameter(description = "单点登录客户端", required = true) @Valid @RequestBody MongoOAuth2ClientDetails domain) {
        log.debug("REST create oauth client detail: {}", domain);
        domain.setClientId(StringUtils.defaultIfEmpty(domain.getClientId(), "" + IdGenerator.generateTimestampId()));
        oAuth2ClientDetailsRepository.findById(domain.getClientId()).ifPresent((existingEntity) -> {
            throw new DuplicationException(ImmutableMap.of("clientId", domain.getClientId()));
        });
        domain.setRawClientSecret(StringUtils.defaultIfEmpty(domain.getClientSecret(), "" + IdGenerator.generateTimestampId()));
        domain.setClientSecret(passwordEncoder.encode(domain.getRawClientSecret()));
        oAuth2ClientDetailsRepository.save(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getClientId()))
                .build();
    }

    @Operation(summary = "分页检索单点登录客户端列表")
    @GetMapping("/api/oauth2-clients")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<MongoOAuth2ClientDetails>> find(Pageable pageable,
                                                               @Parameter(description = "客户端ID") @RequestParam(value = "clientId", required = false) String clientId) {
        Query query = Query.query(Criteria.where("clientId").is(clientId));
        long totalCount = mongoTemplate.count(query, MongoOAuth2ClientDetails.class);
        query.with(pageable);// Note: the field name
        Page<MongoOAuth2ClientDetails> clientDetails = StringUtils.isEmpty(clientId)
                ? oAuth2ClientDetailsRepository.findAll(pageable)
                : new PageImpl<>(mongoTemplate.find(query, MongoOAuth2ClientDetails.class), pageable, totalCount);
        HttpHeaders headers = generatePageHeaders(clientDetails);
        return ResponseEntity.ok().headers(headers).body(clientDetails.getContent());
    }

    @Operation(summary = "根据ID检索单点登录客户端")
    @GetMapping("/api/oauth2-clients/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<MongoOAuth2ClientDetails> findById(
            @Parameter(description = "客户端ID", required = true) @PathVariable String id) {
        MongoOAuth2ClientDetails domain = oAuth2ClientDetailsRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "检索内部单点登录客户端")
    @GetMapping("/open-api/oauth2-client/internal-client")
    public ResponseEntity<Pair<String, String>> findInternalClient() {
        return ResponseEntity.ok(Pair.of(MongoOAuth2ClientDetails.INTERNAL_CLIENT_ID,
                MongoOAuth2ClientDetails.INTERNAL_RAW_CLIENT_SECRET));
    }

    @Operation(summary = "更新单点登录客户端")
    @PutMapping("/api/oauth2-clients")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(
            @Parameter(description = "新的单点登录客户端", required = true) @Valid @RequestBody MongoOAuth2ClientDetails domain) {
        log.debug("REST request to update oauth client detail: {}", domain);
        oAuth2ClientDetailsRepository.findById(domain.getClientId()).orElseThrow(() -> new DataNotFoundException(domain.getClientId()));
        oAuth2ClientDetailsRepository.save(domain);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getClientId()))
                .build();

    }

    @Operation(summary = "根据ID删除单点登录客户端", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/oauth2-clients/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "客户端ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth client detail: {}", id);
        oAuth2ClientDetailsRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oAuth2ClientDetailsRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
