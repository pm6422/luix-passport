package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import io.swagger.annotations.*;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2RefreshToken;
import org.infinity.passport.dto.MongoOAuth2RefreshTokenDTO;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.OAuth2RefreshTokenRepository;
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
@Api(tags = "刷新令牌信息")
public class OAuth2RefreshTokenController {

    private static final Logger LOGGER = LoggerFactory.getLogger(OAuth2RefreshTokenController.class);

    @Autowired
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    @Autowired
    private HttpHeaderCreator httpHeaderCreator;

    @ApiOperation("获取刷新令牌信息分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/oauth2-refresh-token/tokens")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<List<MongoOAuth2RefreshTokenDTO>> find(Pageable pageable,
                                                                 @ApiParam(value = "刷新令牌ID", required = false) @RequestParam(value = "tokenId", required = false) String tokenId,
                                                                 @ApiParam(value = "客户端ID", required = false) @RequestParam(value = "clientId", required = false) String clientId,
                                                                 @ApiParam(value = "用户名", required = false) @RequestParam(value = "userName", required = false) String userName)
            throws URISyntaxException {
        MongoOAuth2RefreshToken probe = new MongoOAuth2RefreshToken();
        probe.setId(tokenId);
        probe.setClientId(clientId);
        probe.setUserName(userName);
        Page<MongoOAuth2RefreshToken> tokens = oAuth2RefreshTokenRepository.findAll(Example.of(probe), pageable);
        List<MongoOAuth2RefreshTokenDTO> DTOs = tokens.getContent().stream().map(entity -> entity.asDTO())
                .collect(Collectors.toList());
        HttpHeaders headers = PaginationUtils.generatePaginationHttpHeaders(tokens, "/api/oauth2-refresh-token/tokens");
        return new ResponseEntity<>(DTOs, headers, HttpStatus.OK);
    }

    @ApiOperation("根据刷新令牌ID检索刷新令牌信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "刷新令牌信息不存在")})
    @GetMapping("/api/oauth2-refresh-token/tokens/{id}")
    @Secured({Authority.ADMIN})
    @Timed
    public ResponseEntity<MongoOAuth2RefreshTokenDTO> findById(
            @ApiParam(value = "刷新令牌ID", required = true) @PathVariable String id) {
        MongoOAuth2RefreshToken entity = oAuth2RefreshTokenRepository.findById(id)
                .orElseThrow(() -> new NoDataException(id));
        return new ResponseEntity<>(entity.asDTO(), HttpStatus.OK);
    }

    @ApiOperation(value = "根据刷新令牌ID删除刷新令牌信息", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "刷新令牌信息不存在")})
    @DeleteMapping("/api/oauth2-refresh-token/tokens/{id}")
    @Secured(Authority.ADMIN)
    @Timed
    public ResponseEntity<Void> delete(@ApiParam(value = "刷新令牌ID", required = true) @PathVariable String id) {
        LOGGER.debug("REST request to delete oauth2 access token: {}", id);
        oAuth2RefreshTokenRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        oAuth2RefreshTokenRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.refresh.token.deleted", id))
                .build();
    }
}
