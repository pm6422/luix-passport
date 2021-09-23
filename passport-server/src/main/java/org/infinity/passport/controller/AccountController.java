package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.config.oauth2.SecurityUser;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.ResetKeyAndPasswordDTO;
import org.infinity.passport.dto.UserNameAndPasswordDTO;
import org.infinity.passport.event.LogoutEvent;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.service.AuthorityService;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserProfilePhotoService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.RandomUtils;
import org.infinity.passport.utils.SecurityUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static org.infinity.passport.utils.NetworkUtils.getRequestUrl;

/**
 * REST controller for managing the user's account.
 */
@RestController
@Api(tags = "账号管理")
@Slf4j
public class AccountController {
    private static final FastDateFormat             DATETIME_FORMAT = FastDateFormat.getInstance("yyyyMMdd-HHmmss");
    @Resource
    private              UserService                userService;
    @Resource
    private              UserAuthorityRepository    userAuthorityRepository;
    @Resource
    private              UserProfilePhotoRepository userProfilePhotoRepository;
    @Resource
    private              UserProfilePhotoService    userProfilePhotoService;
    @Resource
    private              AuthorityService           authorityService;
    @Resource
    private              MailService                mailService;
    @Resource
    private              TokenStore                 tokenStore;
    @Resource
    private              ApplicationEventPublisher  applicationEventPublisher;
    @Resource
    private              HttpHeaderCreator          httpHeaderCreator;

    @ApiOperation(value = "检索访问令牌", notes = "登录成功返回当前访问令牌")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/accounts/access-token")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token) || !token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            return ResponseEntity.ok(StringUtils.EMPTY);
        }
        return ResponseEntity.ok(StringUtils.substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim());
    }

    @ApiOperation(value = "检索当前登录的用户名", notes = "登录成功返回当前用户名")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/accounts/authenticate")
    public ResponseEntity<String> isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return ResponseEntity.ok(request.getRemoteUser());
    }

    @ApiOperation(value = "检索当前登录的用户名", notes = "用于SSO客户端调用，理论上不会返回null，因为未登录则会出错，登录成功返回当前用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/accounts/principal")
    public ResponseEntity<Principal> getPrincipal(Principal user) {
        log.debug("REST request to get current user if the user is authenticated");
        return ResponseEntity.ok(user);
    }

    @ApiOperation("检索当前用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号无权限")})
    @GetMapping("/api/accounts/user")
    @Secured({Authority.USER})
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.findOneByUserName(SecurityUtils.getCurrentUserName());
        List<UserAuthority> userAuthorities = Optional.ofNullable(userAuthorityRepository.findByUserId(user.getId()))
                .orElseThrow(() -> new NoAuthorityException(SecurityUtils.getCurrentUserName()));
        Set<String> authorities = userAuthorities.stream().map(UserAuthority::getAuthorityName).collect(Collectors.toSet());
        user.setAuthorities(authorities);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Signed-In", "true");
        return ResponseEntity.ok().headers(headers).body(user);
    }

    @ApiOperation("根据访问令牌检索绑定的用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/open-api/accounts/user")
    public ResponseEntity<Object> getTokenUser(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (token != null && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(StringUtils
                    .substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim());
            if (oAuth2Authentication != null) {
                User user = userService.findOneByUserName(oAuth2Authentication.getUserAuthentication().getName());
                Set<String> authorities = oAuth2Authentication.getUserAuthentication().getAuthorities().stream()
                        .map(GrantedAuthority::getAuthority).collect(Collectors.toSet());
                if (user != null) {
                    user.setAuthorities(authorities);
                    return ResponseEntity.ok(user);
                }
            }
        }
        // UserInfoTokenServices.loadAuthentication里会判断是否返回结果里包含error字段值，如果返回null会有空指针异常
        // 这个也许是客户端的一个BUG，升级后观察是否已经修复
        return ResponseEntity.ok(ImmutableMap.of("error", true));
    }

    @ApiOperation("注册新用户并发送激活邮件")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号已注册")})
    @PostMapping("/open-api/accounts/register")
    public ResponseEntity<Void> registerAccount(
            @ApiParam(value = "用户", required = true) @Valid @RequestBody ManagedUserDTO dto,
            HttpServletRequest request) {
        log.debug("REST request to register user: {}", dto);
        User newUser = userService.insert(dto.toUser(), dto.getPassword());
        mailService.sendActivationEmail(newUser, getRequestUrl(request));
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2001");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }

    @ApiOperation("根据激活码激活账户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功激活"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "激活码不存在")})
    @GetMapping("/open-api/accounts/activate/{key:[0-9]+}")
    public void activateAccount(@ApiParam(value = "激活码", required = true) @PathVariable String key) {
        userService.activateRegistration(key).orElseThrow(() -> new DataNotFoundException(key));
    }

    @ApiOperation("检索权限值列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/accounts/authority-names")
    @Secured({Authority.USER})
    public ResponseEntity<List<String>> getAuthorityNames(
            @ApiParam(value = "是否可用,null代表全部", allowableValues = "false,true,null") @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<String> authorities = authorityService.find(enabled).stream().map(Authority::getName).collect(Collectors.toList());
        return ResponseEntity.ok(authorities);
    }

    @ApiOperation("更新当前用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户未登录或账号已注册"),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "登录用户已经不存在")})
    @PutMapping("/api/accounts/user")
    @Secured({Authority.USER})
    public ResponseEntity<Void> updateCurrentAccount(@ApiParam(value = "新的用户", required = true) @Valid @RequestBody User domain) {
        // For security reason
        User currentUser = userService.findOneByUserName(SecurityUtils.getCurrentUserName());
        domain.setId(currentUser.getId());
        domain.setUserName(currentUser.getUserName());
        userService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUserName())).build();
    }

    @ApiOperation("修改当前用户的密码")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "密码不正确")})
    @PutMapping("/api/accounts/password")
    @Secured({Authority.USER})
    public ResponseEntity<Void> changePassword(@ApiParam(value = "新密码", required = true) @RequestBody @Valid UserNameAndPasswordDTO dto) {
        // For security reason
        dto.setUserName(SecurityUtils.getCurrentUserName());
        userService.changePassword(dto);
        // Logout asynchronously
        applicationEventPublisher.publishEvent(new LogoutEvent(this));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", "password")).build();
    }

    @ApiOperation("发送重置密码邮件")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功发送"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号不存在")})
    @PostMapping("/open-api/accounts/reset-password/init")
    public ResponseEntity<Void> requestPasswordReset(@ApiParam(value = "电子邮件", required = true) @RequestBody String email,
                                                     HttpServletRequest request) {
        User user = userService.requestPasswordReset(email, RandomUtils.generateResetKey());
        mailService.sendPasswordResetMail(user, getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM2002")).build();
    }

    @ApiOperation("重置密码")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功重置"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "重置码无效或已过期")})
    @PostMapping("/open-api/accounts/reset-password/finish")
    public ResponseEntity<Void> finishPasswordReset(@ApiParam(value = "重置码及新密码", required = true) @Valid @RequestBody ResetKeyAndPasswordDTO dto) {
        userService.completePasswordReset(dto.getNewPassword(), dto.getKey());
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM2003")).build();
    }

    @ApiOperation("上传当前用户头像")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功上传")})
    @PostMapping("/api/accounts/profile-photo/upload")
    @Secured({Authority.USER})
    public void uploadProfilePhoto(@ApiParam(value = "文件描述", required = true) @RequestPart String description,
                                   @ApiParam(value = "用户头像文件", required = true) @RequestPart MultipartFile file) throws IOException {
        log.debug("Upload profile with file name {} and description {}", file.getOriginalFilename(), description);
        User user = userService.findOneByUserName(SecurityUtils.getCurrentUserName());
        userProfilePhotoService.save(user, file.getBytes());
    }

    @ApiOperation("下载用户头像")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功上传")})
    @GetMapping("/api/accounts/profile-photo/download")
    @Secured({Authority.USER})
    public ResponseEntity<org.springframework.core.io.Resource> downloadProfilePhoto() {
        SecurityUser currentUser = SecurityUtils.getCurrentUser();
        Optional<UserProfilePhoto> existingPhoto = userProfilePhotoRepository.findByUserId(currentUser.getUserId());
        if (!existingPhoto.isPresent()) {
            return ResponseEntity.ok().body(null);
        }
        ByteArrayResource resource = new ByteArrayResource(existingPhoto.get().getProfilePhoto().getData());
        String fileName = "profile-" + DATETIME_FORMAT.format(new Date()) + ".jpg";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(existingPhoto.get().getProfilePhoto().getData().length)
                .body(resource);

//        String path = System.getProperty("user.home") + File.separator + "fileName.txt";
//        File outFile = ResourceUtils.getFile(path);
//        FileUtils.writeLines(outFile, strList);
    }

    @ApiOperation("检索当前用户头像")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/accounts/profile-photo")
    @Secured({Authority.USER})
    public ModelAndView getProfilePhoto() {
        // @RestController下使用return forwardUrl不好使
        String forwardUrl = "forward:".concat(UserController.GET_PROFILE_PHOTO_URL).concat(SecurityUtils.getCurrentUserName());
        log.info(forwardUrl);
        return new ModelAndView(forwardUrl);
    }
}
