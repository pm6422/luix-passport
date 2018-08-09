package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.*;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2AccessToken;
import org.infinity.passport.dto.MongoOAuth2AccessTokenDTO;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.OAuth2AccessTokenRepository;
import org.infinity.passport.utils.HttpHeaderCreator;
import org.infinity.passport.utils.PaginationUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import java.net.URISyntaxException;
import java.util.List;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.SC_BAD_REQUEST;
import static javax.servlet.http.HttpServletResponse.SC_OK;

@RestController
@Api(tags = "访问令牌信息")
public class OAuth2AccessTokenController {

    private static final Logger                      LOGGER = LoggerFactory.getLogger(OAuth2AccessTokenController.class);
    @Autowired
    private              OAuth2AccessTokenRepository oAuth2AccessTokenRepository;
    @Autowired
    private              HttpHeaderCreator           httpHeaderCreator;

    @ApiOperation("获取访问令牌信息分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/oauth2-access-token/tokens")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<List<MongoOAuth2AccessTokenDTO>> find(Pageable pageable,
                                                                @ApiParam(value = "访问令牌ID", required = false) @RequestParam(value = "tokenId", required = false) String tokenId,
                                                                @ApiParam(value = "客户端ID", required = false) @RequestParam(value = "clientId", required = false) String clientId,
                                                                @ApiParam(value = "用户名", required = false) @RequestParam(value = "userName", required = false) String userName,
                                                                @ApiParam(value = "刷新令牌", required = false) @RequestParam(value = "refreshToken", required = false) String refreshToken)
            throws URISyntaxException {
        MongoOAuth2AccessToken probe = new MongoOAuth2AccessToken();
        probe.setId(tokenId);
        probe.setClientId(clientId);
        probe.setUserName(userName);
        probe.setRefreshToken(refreshToken);
        Page<MongoOAuth2AccessToken> tokens = oAuth2AccessTokenRepository.findAll(Example.of(probe), pageable);
        List<MongoOAuth2AccessTokenDTO> DTOs = tokens.getContent().stream().map(entity -> entity.asDTO())
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(tokens, "/api/oauth2-access-token/tokens");
        return new ResponseEntity<>(DTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据访问令牌ID检索访问令牌信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "访问令牌信息不存在")})
    @GetMapping("/api/oauth2-access-token/tokens/{id}")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<MongoOAuth2AccessTokenDTO> findById(
            @ApiParam(value = "访问令牌ID", required = true) @PathVariable String id) {
        MongoOAuth2AccessToken entity = oAuth2AccessTokenRepository.findById(id)
                .orElseThrow(() -> new NoDataException(id));
        return ResponseEntity.ok(entity.asDTO());
    }

    @ApiOperation(value = "根据访问令牌ID删除访问令牌信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "访问令牌信息不存在")})
    @DeleteMapping("/api/oauth2-access-token/tokens/{id}")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> delete(@ApiParam(value = "访问令牌ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete oauth2 access token: {}", id);
        oAuth2AccessTokenRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        oAuth2AccessTokenRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.access.token.deleted", id)).build();
    }
}
