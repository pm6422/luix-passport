package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.infinity.passport.dto.MongoOAuth2ClientDetailsDTO;
import org.infinity.passport.exception.DuplicationException;
import org.infinity.passport.exception.NoDataFoundException;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.infinity.passport.utils.id.IdGenerator;
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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Api(tags = "单点登录客户端")
@Slf4j
public class OAuth2ClientDetailsController {

    private final OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

    private final MongoTemplate mongoTemplate;

    private final HttpHeaderCreator httpHeaderCreator;

    private final PasswordEncoder passwordEncoder;

    public OAuth2ClientDetailsController(OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository,
                                         MongoTemplate mongoTemplate, HttpHeaderCreator httpHeaderCreator,
                                         PasswordEncoder passwordEncoder) {
        this.oAuth2ClientDetailsRepository = oAuth2ClientDetailsRepository;
        this.mongoTemplate = mongoTemplate;
        this.httpHeaderCreator = httpHeaderCreator;
        this.passwordEncoder = passwordEncoder;
    }

    @ApiOperation("创建单点登录客户端")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典名已存在")})
    @PostMapping("/api/oauth2-client/clients")
    @Secured(Authority.ADMIN)
    public ResponseEntity<Void> create(
            @ApiParam(value = "单点登录客户端", required = true) @Valid @RequestBody MongoOAuth2ClientDetailsDTO dto) {
        log.debug("REST create oauth client detail: {}", dto);
        dto.setClientId(StringUtils.defaultIfEmpty(dto.getClientId(), "" + IdGenerator.generateSnowFlakeId()));
        oAuth2ClientDetailsRepository.findById(dto.getClientId()).ifPresent((existingEntity) -> {
            throw new DuplicationException(ImmutableMap.of("clientId", dto.getClientId()));
        });
        dto.setRawClientSecret(StringUtils.defaultIfEmpty(dto.getClientSecret(), "" + IdGenerator.generateSnowFlakeId()));
        dto.setClientSecret(passwordEncoder.encode(dto.getRawClientSecret()));
        oAuth2ClientDetailsRepository.save(MongoOAuth2ClientDetails.of(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", dto.getClientId()))
                .build();
    }

    @ApiOperation("分页检索单点登录客户端列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/oauth2-client/clients")
    @Secured(Authority.ADMIN)
    public ResponseEntity<List<MongoOAuth2ClientDetailsDTO>> find(Pageable pageable,
                                                                  @ApiParam(value = "客户端ID") @RequestParam(value = "clientId", required = false) String clientId)
            throws URISyntaxException {
        Query query = Query.query(Criteria.where("clientId").is(clientId));
        long totalCount = mongoTemplate.count(query, MongoOAuth2ClientDetails.class);
        query.with(pageable);// Note: the field name
        Page<MongoOAuth2ClientDetails> clientDetails = StringUtils.isEmpty(clientId)
                ? oAuth2ClientDetailsRepository.findAll(pageable)
                : new PageImpl<>(mongoTemplate.find(query, MongoOAuth2ClientDetails.class), pageable, totalCount);
        List<MongoOAuth2ClientDetailsDTO> DTOs = clientDetails.getContent().stream()
                .map(MongoOAuth2ClientDetails::toDTO).collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(clientDetails);
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据ID检索单点登录客户端")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端不存在")})
    @GetMapping("/api/oauth2-client/clients/{id}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<MongoOAuth2ClientDetailsDTO> findById(
            @ApiParam(value = "客户端ID", required = true) @PathVariable String id) {
        MongoOAuth2ClientDetails entity = oAuth2ClientDetailsRepository.findById(id).orElseThrow(() -> new NoDataFoundException(id));
        return ResponseEntity.ok(entity.toDTO());
    }

    @ApiOperation("检索内部单点登录客户端")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端不存在")})
    @GetMapping("/open-api/oauth2-client/internal-client")
    public ResponseEntity<Pair<String, String>> findInternalClient() {
        return ResponseEntity.ok(Pair.of(MongoOAuth2ClientDetails.INTERNAL_CLIENT_ID,
                MongoOAuth2ClientDetails.INTERNAL_RAW_CLIENT_SECRET));
    }

    @ApiOperation("更新单点登录客户端")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端不存在")})
    @PutMapping("/api/oauth2-client/clients")
    @Secured(Authority.ADMIN)
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的单点登录客户端", required = true) @Valid @RequestBody MongoOAuth2ClientDetailsDTO dto) {
        log.debug("REST request to update oauth client detail: {}", dto);
        oAuth2ClientDetailsRepository.findById(dto.getClientId()).orElseThrow(() -> new NoDataFoundException(dto.getClientId()));
        oAuth2ClientDetailsRepository.save(MongoOAuth2ClientDetails.of(dto));
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1002", dto.getClientId()))
                .build();

    }

    @ApiOperation(value = "根据ID删除单点登录客户端", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端不存在")})
    @DeleteMapping("/api/oauth2-client/clients/{id}")
    @Secured(Authority.ADMIN)
    public ResponseEntity<Void> delete(@ApiParam(value = "客户端ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth client detail: {}", id);
        oAuth2ClientDetailsRepository.findById(id).orElseThrow(() -> new NoDataFoundException(id));
        oAuth2ClientDetailsRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
