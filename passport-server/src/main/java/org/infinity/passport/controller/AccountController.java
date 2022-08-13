package org.infinity.passport.controller;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.config.oauth2.SecurityUser;
import org.infinity.passport.config.oauth2.SecurityUserDetailsServiceImpl;
import org.infinity.passport.config.oauth2.SecurityUtils;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.ResetKeyAndPasswordDTO;
import org.infinity.passport.dto.UserNameAndPasswordDTO;
import org.infinity.passport.config.oauth2.LogoutEvent;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.service.AuthorityService;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserProfilePhotoService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.RandomUtils;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.server.authorization.OAuth2AuthorizationService;
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

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.NetworkUtils.getRequestUrl;

/**
 * REST controller for managing the user's account.
 */
@RestController
@Tag(name = "账号管理")
@SecurityRequirement(name = AUTH)
@Slf4j
public class AccountController {
    private static final FastDateFormat                 DATETIME_FORMAT = FastDateFormat.getInstance("yyyyMMdd-HHmmss");
    @Resource
    private              UserService                    userService;
    @Resource
    private              UserAuthorityRepository        userAuthorityRepository;
    @Resource
    private              UserProfilePhotoRepository     userProfilePhotoRepository;
    @Resource
    private              UserProfilePhotoService        userProfilePhotoService;
    @Resource
    private              AuthorityService               authorityService;
    @Resource
    private              MailService                    mailService;
    @Resource
    private              OAuth2AuthorizationService     oAuth2AuthorizationService;
    @Resource
    private              SecurityUserDetailsServiceImpl userDetailsService;
    @Resource
    private              ApplicationEventPublisher      applicationEventPublisher;
    @Resource
    private              HttpHeaderCreator              httpHeaderCreator;

    @Operation(summary = "检索访问令牌", description = "登录成功返回当前访问令牌")
    @GetMapping("/api/accounts/access-token")
    @Timed
    public ResponseEntity<String> getAccessToken(HttpServletRequest request) {
        String token = request.getHeader("authorization");
        if (StringUtils.isEmpty(token) || !token.toLowerCase().startsWith(OAuth2AccessToken.BEARER_TYPE.toLowerCase())) {
            return ResponseEntity.ok(StringUtils.EMPTY);
        }
        return ResponseEntity.ok(StringUtils.substringAfter(token.toLowerCase(), OAuth2AccessToken.BEARER_TYPE.toLowerCase()).trim());
    }

    @Operation(summary = "检索当前登录的用户名", description = "登录成功返回当前用户名")
    @GetMapping("/api/accounts/authenticate")
    @Timed
    public ResponseEntity<String> isAuthenticated(HttpServletRequest request) {
        log.debug("REST request to check if the current user is authenticated");
        return ResponseEntity.ok(request.getRemoteUser());
    }

    @Operation(summary = "检索当前登录的用户名", description = "用于SSO客户端调用，理论上不会返回null，因为未登录则会出错，登录成功返回当前用户")
    @GetMapping("/api/accounts/principal")
    @Timed
    public ResponseEntity<Principal> getPrincipal(Principal user) {
        log.debug("REST request to get current user if the user is authenticated");
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "检索当前用户")
    @GetMapping("/api/accounts/user")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.findOneByUsername(SecurityUtils.getCurrentUsername());
        List<UserAuthority> userAuthorities = Optional.ofNullable(userAuthorityRepository.findByUserId(user.getId()))
                .orElseThrow(() -> new NoAuthorityException(SecurityUtils.getCurrentUsername()));
        Set<String> authorities = userAuthorities.stream().map(UserAuthority::getAuthorityName).collect(Collectors.toSet());
        user.setAuthorities(authorities);
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Signed-In", "true");
        return ResponseEntity.ok().headers(headers).body(user);
    }

    @Operation(summary = "根据访问令牌检索绑定的用户")
    @GetMapping("/open-api/accounts/user")
    @Timed
    public ResponseEntity<Object> getTokenUser(HttpServletRequest request) {
        SecurityUser securityUser = userDetailsService.getUserByAccessToken(request);
        if (securityUser == null) {
            // UserInfoTokenServices.loadAuthentication里会判断是否返回结果里包含error字段值，如果返回null会有空指针异常
            // 这个也许是客户端的一个BUG，升级后观察是否已经修复
            return ResponseEntity.ok(ImmutableMap.of("error", true));
        }
        User user = userService.findOneByUsername(securityUser.getUsername());
        user.setAuthorities(securityUser.getAuthorities().stream().map(GrantedAuthority::getAuthority).collect(Collectors.toSet()));
        return ResponseEntity.ok(user);
    }

    @Operation(summary = "注册新用户并发送激活邮件")
    @PostMapping("/open-api/accounts/register")
    @Timed
    public ResponseEntity<Void> registerAccount(
            @Parameter(description = "用户", required = true) @Valid @RequestBody ManagedUserDTO dto,
            HttpServletRequest request) {
        log.debug("REST request to register user: {}", dto);
        User newUser = userService.insert(dto.toUser(), dto.getPassword());
        mailService.sendActivationEmail(newUser, getRequestUrl(request));
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2001");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }

    @Operation(summary = "根据激活码激活账户")
    @GetMapping("/open-api/accounts/activate/{key:[0-9]+}")
    @Timed
    public void activateAccount(@Parameter(description = "激活码", required = true) @PathVariable String key) {
        userService.activateRegistration(key).orElseThrow(() -> new DataNotFoundException(key));
    }

    @Operation(summary = "检索权限值列表")
    @GetMapping("/api/accounts/authority-names")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public ResponseEntity<List<String>> getAuthorityNames(
            @Parameter(description = "是否可用,null代表全部", schema = @Schema(allowableValues = "false,true,null"))
            @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<String> authorities = authorityService.find(enabled).stream().map(Authority::getName).collect(Collectors.toList());
        return ResponseEntity.ok(authorities);
    }

    @Operation(summary = "更新当前用户")
    @PutMapping("/api/accounts/user")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public ResponseEntity<Void> updateCurrentAccount(@Parameter(description = "新的用户", required = true) @Valid @RequestBody User domain) {
        // For security reason
        User currentUser = userService.findOneByUsername(SecurityUtils.getCurrentUsername());
        domain.setId(currentUser.getId());
        domain.setUsername(currentUser.getUsername());
        userService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUsername())).build();
    }

    @Operation(summary = "修改当前用户的密码")
    @PutMapping("/api/accounts/password")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public ResponseEntity<Void> changePassword(@Parameter(description = "新密码", required = true) @RequestBody @Valid UserNameAndPasswordDTO dto) {
        // For security reason
        dto.setUsername(SecurityUtils.getCurrentUsername());
        userService.changePassword(dto);
        // Logout asynchronously
        applicationEventPublisher.publishEvent(new LogoutEvent(this));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", "password")).build();
    }

    @Operation(summary = "发送重置密码邮件")
    @PostMapping("/open-api/accounts/reset-password/init")
    @Timed
    public ResponseEntity<Void> requestPasswordReset(@Parameter(description = "电子邮件", required = true) @RequestBody String email,
                                                     HttpServletRequest request) {
        User user = userService.requestPasswordReset(email, RandomUtils.generateResetKey());
        mailService.sendPasswordResetMail(user, getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM2002")).build();
    }

    @Operation(summary = "重置密码")
    @PostMapping("/open-api/accounts/reset-password/finish")
    @Timed
    public ResponseEntity<Void> finishPasswordReset(@Parameter(description = "重置码及新密码", required = true) @Valid @RequestBody ResetKeyAndPasswordDTO dto) {
        userService.completePasswordReset(dto.getNewPassword(), dto.getKey());
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM2003")).build();
    }

    @Operation(summary = "上传当前用户头像")
    @PostMapping("/api/accounts/profile-photo/upload")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public void uploadProfilePhoto(@Parameter(description = "文件描述", required = true) @RequestPart String description,
                                   @Parameter(description = "用户头像文件", required = true) @RequestPart MultipartFile file) throws IOException {
        log.debug("Upload profile with file name {} and description {}", file.getOriginalFilename(), description);
        User user = userService.findOneByUsername(SecurityUtils.getCurrentUsername());
        userProfilePhotoService.save(user, file.getBytes());
    }

    @Operation(summary = "下载用户头像")
    @GetMapping("/api/accounts/profile-photo/download")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public ResponseEntity<org.springframework.core.io.Resource> downloadProfilePhoto() {
        SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(SecurityUtils.getCurrentUsername());
        Optional<UserProfilePhoto> existingPhoto = userProfilePhotoRepository.findByUserId(userDetails.getId());
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

    @Operation(summary = "检索当前用户头像")
    @GetMapping("/api/accounts/profile-photo")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    @Timed
    public ModelAndView getProfilePhoto() {
        // @RestController下使用return forwardUrl不好使
        String forwardUrl = "forward:".concat(UserController.GET_PROFILE_PHOTO_URL).concat(SecurityUtils.getCurrentUsername());
        log.info(forwardUrl);
        return new ModelAndView(forwardUrl);
    }
}
