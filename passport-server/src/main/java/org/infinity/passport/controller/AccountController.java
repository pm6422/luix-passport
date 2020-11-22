package org.infinity.passport.controller;

import com.google.common.collect.ImmutableMap;
import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.ResetKeyAndPasswordDTO;
import org.infinity.passport.dto.UserDTO;
import org.infinity.passport.exception.FieldValidationException;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.service.AuthorityService;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserProfilePhotoService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.RandomUtils;
import org.infinity.passport.utils.SecurityUtils;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.TokenStore;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.security.Principal;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;

/**
 * REST controller for managing the user's account.
 */
@RestController
@Api(tags = "账号管理")
@Slf4j
public class AccountController {

    private final UserService                userService;
    private final UserRepository             userRepository;
    private final UserAuthorityRepository    userAuthorityRepository;
    private final UserProfilePhotoRepository userProfilePhotoRepository;
    private final UserProfilePhotoService    userProfilePhotoService;
    private final AuthorityService           authorityService;
    private final MailService                mailService;
    private final HttpHeaderCreator          httpHeaderCreator;
    private final TokenStore                 tokenStore;

    public AccountController(UserService userService,
                             UserRepository userRepository,
                             UserAuthorityRepository userAuthorityRepository,
                             UserProfilePhotoRepository userProfilePhotoRepository,
                             UserProfilePhotoService userProfilePhotoService,
                             AuthorityService authorityService,
                             MailService mailService,
                             HttpHeaderCreator httpHeaderCreator,
                             TokenStore tokenStore) {
        this.userService = userService;
        this.userRepository = userRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.userProfilePhotoRepository = userProfilePhotoRepository;
        this.userProfilePhotoService = userProfilePhotoService;
        this.authorityService = authorityService;
        this.mailService = mailService;
        this.httpHeaderCreator = httpHeaderCreator;
        this.tokenStore = tokenStore;
    }

    @ApiOperation(value = "获取访问令牌", notes = "登录成功返回当前访问令牌", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/account/access-token")
    public ResponseEntity<String> getAccessToken(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (token != null && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            return ResponseEntity.ok(
                    StringUtils.substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim());
        }
        return ResponseEntity.ok(StringUtils.EMPTY);
    }

    @ApiOperation(value = "验证当前用户是否已经登录，理论上不会返回false，因为未登录则会出错", notes = "登录成功返回当前用户名", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/account/authenticate")
    public ResponseEntity<String> isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return ResponseEntity.ok(request.getRemoteUser());
    }

    @ApiOperation(value = "获取登录的用户,用于SSO客户端调用，理论上不会返回null，因为未登录则会出错", notes = "登录成功返回当前用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/account/principal")
    public ResponseEntity<Principal> getPrincipal(Principal user) {
        log.debug("REST request to get current user if the user is authenticated");
        return ResponseEntity.ok(user);
    }

    @ApiOperation("获取当前用户信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号无权限")})
    @GetMapping("/api/account/user")
    @Secured({Authority.USER})
    public ResponseEntity<UserDTO> getCurrentUser() {
        User user = userService.findOneByUserName(SecurityUtils.getCurrentUserName()).orElseThrow(() -> new NoDataException(SecurityUtils.getCurrentUserName()));
        List<UserAuthority> userAuthorities = userAuthorityRepository.findByUserId(user.getId());

        if (CollectionUtils.isEmpty(userAuthorities)) {
            throw new NoAuthorityException(SecurityUtils.getCurrentUserName());
        }
        Set<String> authorities = userAuthorities.stream().map(UserAuthority::getAuthorityName).collect(Collectors.toSet());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Signed-In", "true");
        return ResponseEntity.ok().headers(headers).body(new UserDTO(user, authorities));
    }

    @ApiOperation("根据访问令牌信息获取绑定的用户信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/open-api/account/user")
    public ResponseEntity<Object> getTokenUser(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (token != null && token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            OAuth2Authentication oAuth2Authentication = tokenStore.readAuthentication(StringUtils
                    .substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim());
            if (oAuth2Authentication != null) {
                Optional<User> user = userService
                        .findOneByUserName(oAuth2Authentication.getUserAuthentication().getName());
                if (user.isPresent()) {
                    return ResponseEntity.ok(
                            new UserDTO(user.get(),
                                    oAuth2Authentication.getUserAuthentication().getAuthorities().stream()
                                            .map(GrantedAuthority::getAuthority).collect(Collectors.toSet())));
                }
            }
        }
        // UserInfoTokenServices.loadAuthentication里会判断是否返回结果里包含error字段值，如果返回null会有空指针异常
        // 这个也许是客户端的一个BUG，升级后观察是否已经修复
        return ResponseEntity.ok(ImmutableMap.of("error", true));
    }

    @ApiOperation("注册新用户")
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号已注册")})
    @PostMapping("/open-api/account/register")
    public ResponseEntity<Void> registerAccount(
            @ApiParam(value = "用户信息", required = true) @Valid @RequestBody ManagedUserDTO managedUserDTO,
            HttpServletRequest request) {
        User newUser = userService.insert(managedUserDTO.getUserName(), managedUserDTO.getPassword(),
                managedUserDTO.getFirstName(), managedUserDTO.getLastName(), managedUserDTO.getEmail(),
                managedUserDTO.getMobileNo(), RandomUtils.generateActivationKey(), false,
                true, null, null, null, null);
        String baseUrl = request.getScheme() + // "http"
                "://" + // "://"
                request.getServerName() + // "host"
                ":" + // ":"
                request.getServerPort() + // "80"
                request.getContextPath(); // "/myContextPath" or "" if deployed in root context

        mailService.sendActivationEmail(newUser, baseUrl);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("notification.registration.success")).build();
    }

    @ApiOperation("根据激活码激活账户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功激活"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "激活码不存在")})
    @GetMapping("/open-api/account/activate/{key:[0-9]+}")
    public void activateAccount(@ApiParam(value = "激活码", required = true) @PathVariable String key) {
        userService.activateRegistration(key).orElseThrow(() -> new NoDataException(key));
    }

    @ApiOperation("获取权限值列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/account/authority-names")
    @Secured({Authority.USER})
    public ResponseEntity<List<String>> getAuthorityNames(
            @ApiParam(value = "是否可用,null代表全部", allowableValues = "false,true,null") @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<String> authorities = authorityService.find(enabled).stream().map(Authority::getName).collect(Collectors.toList());
        return ResponseEntity.ok(authorities);
    }

    @ApiOperation("更新当前用户信息")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户未登录或账号已注册"),
            @ApiResponse(code = SC_INTERNAL_SERVER_ERROR, message = "登录用户信息已经不存在")})
    @PutMapping("/api/account/user")
    @Secured({Authority.USER})
    public ResponseEntity<Void> updateCurrentAccount(
            @ApiParam(value = "新的用户信息", required = true) @Valid @RequestBody UserDTO dto) {
        userService.updateWithCheck(dto);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.user.updated", dto.getUserName()))
                .build();
    }

    @ApiOperation("修改当前用户的密码")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "密码不正确")})
    @PutMapping("/api/account/password")
    @Secured({Authority.USER})
    public ResponseEntity<Void> changePassword(@ApiParam(value = "新密码", required = true) @RequestBody String newPassword) {
        userService.changePassword(SecurityUtils.getCurrentUserName(), newPassword);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.password.changed")).build();
    }

    @ApiOperation("发送重置密码邮件")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功发送"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号不存在")})
    @PostMapping("/open-api/account/reset-password/init")
    public ResponseEntity<Void> requestPasswordReset(
            @ApiParam(value = "电子邮件", required = true) @RequestBody String email, HttpServletRequest request) {
        User user = userService.requestPasswordReset(email, RandomUtils.generateResetKey()).orElseThrow(
                () -> new FieldValidationException("email", "email", email, "error.email.not.exist", email));
        String baseUrl = request.getScheme()
                + "://"
                + request.getServerName()
                + ":"
                + request.getServerPort()
                + request.getContextPath();
        mailService.sendPasswordResetMail(user, baseUrl);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.password.reset.email.sent")).build();
    }

    @ApiOperation("重置密码")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功重置"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "重置码无效或已过期")})
    @PostMapping("/open-api/account/reset-password/finish")
    public ResponseEntity<Void> finishPasswordReset(
            @ApiParam(value = "重置码及新密码信息", required = true) @Valid @RequestBody ResetKeyAndPasswordDTO resetKeyAndPasswordDTO) {
        userService.completePasswordReset(resetKeyAndPasswordDTO.getNewPassword(), resetKeyAndPasswordDTO.getKey())
                .orElseThrow(() -> new FieldValidationException("resetKeyAndPasswordDTO", "key",
                        resetKeyAndPasswordDTO.getKey(), "error.invalid.reset.key"));
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("notification.password.reset")).build();

    }

    @ApiOperation("上传用户头像")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功上传")})
    @PostMapping("/api/account/profile-photo/upload")
    @Secured({Authority.USER})
    public void uploadProfilePhoto(@ApiParam(value = "文件描述", required = true) @RequestPart String description,
                                   @ApiParam(value = "用户头像文件", required = true) @RequestPart MultipartFile file) throws IOException {
        log.debug("upload file with name {} and description {}", file.getOriginalFilename(), description);
        Optional<UserProfilePhoto> existingPhoto = userProfilePhotoRepository.findByUserName(SecurityUtils.getCurrentUserName());
        if (existingPhoto.isPresent()) {
            // Update if existing
            userProfilePhotoService.update(existingPhoto.get().getId(), existingPhoto.get().getUserName(), file.getBytes());
        } else {
            // Insert if not existing
            userProfilePhotoService.insert(SecurityUtils.getCurrentUserName(), file.getBytes());
            userRepository.findOneByUserName(SecurityUtils.getCurrentUserName()).ifPresent((user) -> {
                // update hasProfilePhoto to true
                user.setHasProfilePhoto(true);
                userRepository.save(user);
            });
        }
    }

    @ApiOperation("获取用户头像")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功获取")})
    @GetMapping("/api/account/profile-photo")
    @Secured({Authority.USER})
    public ModelAndView getProfilePhoto() {
        // @RestController下使用return forwardUrl不好使
        String forwardUrl = "forward:".concat(UserController.GET_PROFILE_PHOTO_URL).concat(SecurityUtils.getCurrentUserName());
        log.info(forwardUrl);
        return new ModelAndView(forwardUrl);
    }
}
