package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2Approval;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.OAuth2ApprovalRepository;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.List;

import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Tag(name = "登录授权")
@Slf4j
public class OAuth2ApprovalController {

    @Resource
    private OAuth2ApprovalRepository oAuth2ApprovalRepository;
    @Resource
    private MongoTemplate            mongoTemplate;
    @Resource
    private HttpHeaderCreator        httpHeaderCreator;

    @Operation(summary = "分页检索登录授权列表")
    @GetMapping("/api/oauth2-approvals")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<MongoOAuth2Approval>> find(Pageable pageable,
                                                          @Parameter(description = "授权ID") @RequestParam(value = "approvalId", required = false) String approvalId,
                                                          @Parameter(description = "客户端ID") @RequestParam(value = "clientId", required = false) String clientId,
                                                          @Parameter(description = "用户名") @RequestParam(value = "userName", required = false) String userName) {
        Query query = new Query();
        if (StringUtils.isNotEmpty(approvalId)) {
            query.addCriteria(Criteria.where("id").is(approvalId));
        }
        if (StringUtils.isNotEmpty(clientId)) {
            query.addCriteria(Criteria.where("clientId").is(clientId));
        }
        if (StringUtils.isNotEmpty(userName)) {
            query.addCriteria(Criteria.where("userId").is(userName));
        }
        long totalCount = mongoTemplate.count(query, MongoOAuth2Approval.class);
        query.with(pageable);
        Page<MongoOAuth2Approval> approvals = new PageImpl<>(mongoTemplate.find(query, MongoOAuth2Approval.class), pageable, totalCount);
        HttpHeaders headers = generatePageHeaders(approvals);
        return ResponseEntity.ok().headers(headers).body(approvals.getContent());
    }

    @Operation(summary = "根据ID检索授权")
    @GetMapping("/api/oauth2-approvals/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<MongoOAuth2Approval> findById(
            @Parameter(description = "授权ID", required = true) @PathVariable String id) {
        MongoOAuth2Approval domain = oAuth2ApprovalRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "根据ID删除授权", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/oauth2-approvals/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "授权ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth2 approval: {}", id);
        oAuth2ApprovalRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oAuth2ApprovalRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
