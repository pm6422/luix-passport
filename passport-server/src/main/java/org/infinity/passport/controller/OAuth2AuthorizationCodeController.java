package org.infinity.passport.controller;

import io.swagger.annotations.*;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2AuthorizationCode;
import org.infinity.passport.dto.MongoOAuth2AuthorizationCodeDTO;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.OAuth2AuthorizationCodeRepository;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController
@Api(tags = "登录授权码信息")
public class OAuth2AuthorizationCodeController {

    private static final Logger                            LOGGER = LoggerFactory.getLogger(OAuth2AuthorizationCodeController.class);
    @Autowired
    private              OAuth2AuthorizationCodeRepository oAuth2AuthorizationCodeRepository;
    @Autowired
    private              HttpHeaderCreator                 httpHeaderCreator;

    /**
     * Authorization code will be deleted immediately after authentication process.
     * So the database will be always empty.
     *
     * @param pageable
     * @param authorizationCodelId
     * @param code
     * @return
     * @throws URISyntaxException
     */
    @ApiOperation("获取授权码信息信息分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/oauth2-authorization-code/codes")
    @Secured(Authority.ADMIN)
    public ResponseEntity<List<MongoOAuth2AuthorizationCodeDTO>> find(Pageable pageable,
                                                                      @ApiParam(value = "授权码ID", required = false) @RequestParam(value = "authorizationCodelId", required = false) String authorizationCodelId,
                                                                      @ApiParam(value = "授权码", required = false) @RequestParam(value = "code", required = false) String code)
            throws URISyntaxException {
        MongoOAuth2AuthorizationCode probe = new MongoOAuth2AuthorizationCode();
        probe.setId(authorizationCodelId);
        probe.setCode(code);
        Page<MongoOAuth2AuthorizationCode> codes = oAuth2AuthorizationCodeRepository.findAll(Example.of(probe),
                pageable);
        List<MongoOAuth2AuthorizationCodeDTO> DTOs = codes.getContent().stream().map(entity -> entity.asDTO())
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(codes,
                "/api/oauth2-authorization-code/codes");
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据授权码信息ID检索授权码信息信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "授权码信息信息不存在")})
    @GetMapping("/api/oauth2-authorization-code/codes/{id}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<MongoOAuth2AuthorizationCodeDTO> findById(
            @ApiParam(value = "授权码信息ID", required = true) @PathVariable String id) {
        MongoOAuth2AuthorizationCode entity = oAuth2AuthorizationCodeRepository.findById(id)
                .orElseThrow(() -> new NoDataException(id));
        return ResponseEntity.ok(entity.asDTO());
    }

    @ApiOperation(value = "根据授权码信息ID删除授权码信息信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "授权码信息信息不存在")})
    @DeleteMapping("/api/oauth2-authorization-code/codes/{id}")
    @Secured(Authority.ADMIN)
    public ResponseEntity<Void> delete(@ApiParam(value = "授权码信息ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete oauth2 authorization code: {}", id);
        oAuth2AuthorizationCodeRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        oAuth2AuthorizationCodeRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.authorization.code.deleted", id))
                .build();
    }
}
