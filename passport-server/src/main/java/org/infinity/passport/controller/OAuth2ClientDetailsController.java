package org.infinity.passport.controller;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_CREATED;
import static javax.servlet.http.HttpServletResponse.SC_OK;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.infinity.passport.dto.MongoOAuth2ClientDetailsDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.infinity.passport.utils.RandomUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
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
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.codahale.metrics.annotation.Timed;

import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import io.swagger.annotations.ApiResponse;
import io.swagger.annotations.ApiResponses;

@RestController
@Api(tags = "单点登录客户端信息")
public class OAuth2ClientDetailsController {

    private static final Logger           LOGGER = LoggerFactory.getLogger(OAuth2ClientDetailsController.class);

    @Autowired
    private OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

    @Autowired
    private MongoTemplate                 mongoTemplate;

    @Autowired
    private HttpHeaderCreator             httpHeaderCreator;

    @Autowired
    private PasswordEncoder               passwordEncoder;

    @ApiOperation("创建单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "字典名已存在") })
    @PostMapping("/api/oauth2-client/clients")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> create(
            @ApiParam(value = "单点登录客户端信息", required = true) @Valid @RequestBody MongoOAuth2ClientDetailsDTO dto) {
        LOGGER.debug("REST create oauth client detail: {}", dto);
        oAuth2ClientDetailsRepository.findById(dto.getClientId()).ifPresent((existingEntity) -> {
            throw new FieldValidationException("oAuth2ClientDetailsDTO", "clientId", dto.getClientId(),
                    "error.oauth2.client.id.exists", dto.getClientId());
        });
        dto.setClientId(StringUtils.defaultIfEmpty(dto.getClientId(), RandomUtils.generateId()));
        dto.setRawClientSecret(StringUtils.defaultIfEmpty(dto.getClientSecret(), RandomUtils.generateId()));
        dto.setClientSecret(passwordEncoder.encode(dto.getRawClientSecret()));
        oAuth2ClientDetailsRepository.save(MongoOAuth2ClientDetails.of(dto));
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.client.created", dto.getClientId()))
                .build();
    }

    @ApiOperation("获取单点登录客户端信息分页列表")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取") })
    @GetMapping("/api/oauth2-client/clients")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<List<MongoOAuth2ClientDetailsDTO>> getClients(Pageable pageable,
            @ApiParam(value = "客户端ID", required = false) @RequestParam(value = "clientId", required = false) String clientId)
            throws URISyntaxException {
        Query query = Query.query(Criteria.where("clientId").is(clientId)).with(pageable);// Note: the field name
        Page<MongoOAuth2ClientDetails> clientDetails = StringUtils.isEmpty(clientId)
                ? oAuth2ClientDetailsRepository.findAll(pageable)
                : new PageImpl<MongoOAuth2ClientDetails>(mongoTemplate.find(query, MongoOAuth2ClientDetails.class),
                        pageable, mongoTemplate.count(query, MongoOAuth2ClientDetails.class));
        List<MongoOAuth2ClientDetailsDTO> clientDetailsDTOs = clientDetails.getContent().stream()
                .map(entity -> entity.asDTO()).collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(clientDetails,
                "/api/oauth2-client/clients");
        return new ResponseEntity<>(clientDetailsDTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据客户端ID检索单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端信息不存在") })
    @GetMapping("/api/oauth2-client/clients/{id}")
    @Secured({ Authority.ADMIN })
    @Timed
    public ResponseEntity<MongoOAuth2ClientDetailsDTO> getClient(
            @ApiParam(value = "客户端ID", required = true) @PathVariable String id) {
        MongoOAuth2ClientDetails entity = oAuth2ClientDetailsRepository.findById(id)
                .orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(entity.asDTO(), HttpStatus.OK);
    }

    @ApiOperation("获取内部检索单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端信息不存在") })
    @GetMapping("/open-api/oauth2-client/internal-client")
    @Timed
    public ResponseEntity<Pair<String, String>> getInternalClient() {
        return new ResponseEntity<>(Pair.of(MongoOAuth2ClientDetails.INTERNAL_CLIENT_ID,
                MongoOAuth2ClientDetails.INTERNAL_RAW_CLIENT_SECRET), HttpStatus.OK);
    }

    @ApiOperation("更新单点登录客户端信息")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端信息不存在") })
    @PutMapping("/api/oauth2-client/clients")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> update(
            @ApiParam(value = "新的单点登录客户端信息", required = true) @Valid @RequestBody MongoOAuth2ClientDetailsDTO dto) {
        LOGGER.debug("REST request to update oauth client detail: {}", dto);
        oAuth2ClientDetailsRepository.findById(dto.getClientId())
                .orElseThrow(() -> new NoDataException(dto.getClientId()));
        oAuth2ClientDetailsRepository.save(MongoOAuth2ClientDetails.of(dto));
        return ResponseEntity.status(HttpStatus.OK)
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.client.updated", dto.getClientId()))
                .build();

    }

    @ApiOperation(value = "根据客户端ID删除单点登录客户端信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = { @ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "单点登录客户端信息不存在") })
    @DeleteMapping("/api/oauth2-client/clients/{id}")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> delete(@ApiParam(value = "客户端ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete oauth client detail: {}", id);
        oAuth2ClientDetailsRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        oAuth2ClientDetailsRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.client.deleted", id)).build();
    }
}
