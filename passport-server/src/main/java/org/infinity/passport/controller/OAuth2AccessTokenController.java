package org.infinity.passport.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.MongoOAuth2AccessToken;
import org.infinity.passport.dto.MongoOAuth2AccessTokenDTO;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.OAuth2AccessTokenRepository;
import org.infinity.passport.utils.HttpHeaderCreator;
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
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

@RestController
@Api(tags = "访问令牌信息")
@Slf4j
public class OAuth2AccessTokenController {

    private final OAuth2AccessTokenRepository oAuth2AccessTokenRepository;
    private final HttpHeaderCreator           httpHeaderCreator;

    public OAuth2AccessTokenController(OAuth2AccessTokenRepository oAuth2AccessTokenRepository,
                                       HttpHeaderCreator httpHeaderCreator) {
        this.oAuth2AccessTokenRepository = oAuth2AccessTokenRepository;
        this.httpHeaderCreator = httpHeaderCreator;
    }

    @ApiOperation("获取访问令牌信息分页列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/oauth2-access-token/tokens")
    @Secured(Authority.ADMIN)
    public ResponseEntity<List<MongoOAuth2AccessTokenDTO>> find(Pageable pageable,
                                                                @ApiParam(value = "访问令牌ID") @RequestParam(value = "tokenId", required = false) String tokenId,
                                                                @ApiParam(value = "客户端ID") @RequestParam(value = "clientId", required = false) String clientId,
                                                                @ApiParam(value = "用户名") @RequestParam(value = "userName", required = false) String userName,
                                                                @ApiParam(value = "刷新令牌") @RequestParam(value = "refreshToken", required = false) String refreshToken)
            throws URISyntaxException {
        MongoOAuth2AccessToken probe = new MongoOAuth2AccessToken();
        probe.setId(tokenId);
        probe.setClientId(clientId);
        probe.setUserName(userName);
        probe.setRefreshToken(refreshToken);
        Page<MongoOAuth2AccessToken> tokens = oAuth2AccessTokenRepository.findAll(Example.of(probe), pageable);
        List<MongoOAuth2AccessTokenDTO> DTOs = tokens.getContent().stream().map(MongoOAuth2AccessToken::asDTO)
                .collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(tokens, "/api/oauth2-access-token/tokens");
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据访问令牌ID检索访问令牌信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "访问令牌信息不存在")})
    @GetMapping("/api/oauth2-access-token/tokens/{id}")
    @Secured({Authority.ADMIN})
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
    public ResponseEntity<Void> delete(@ApiParam(value = "访问令牌ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth2 access token: {}", id);
        oAuth2AccessTokenRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        oAuth2AccessTokenRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.oauth2.access.token.deleted", id)).build();
    }
}
