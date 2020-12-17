package org.infinity.passport.controller;

import io.swagger.annotations.*;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.UserDTO;
import org.infinity.passport.dto.UserNameAndPasswordDTO;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.exception.NoDataFoundException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.repository.UserRepository;
import org.infinity.passport.security.AjaxLogoutSuccessHandler;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.utils.RandomUtils;
import org.infinity.passport.utils.SecurityUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.annotation.Secured;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.net.URISyntaxException;
import java.time.Instant;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static javax.servlet.http.HttpServletResponse.*;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing users.
 */
@RestController
@Api(tags = "用户管理")
@Slf4j
public class UserController {

    private final        UserRepository             userRepository;
    private final        UserProfilePhotoRepository userProfilePhotoRepository;
    private final        UserAuthorityRepository    userAuthorityRepository;
    private final        UserService                userService;
    private final        MailService                mailService;
    private final        AjaxLogoutSuccessHandler   ajaxLogoutSuccessHandler;
    private final        HttpHeaderCreator          httpHeaderCreator;
    private static final String                     DEFAULT_PASSWORD = "123456";

    public UserController(UserRepository userRepository,
                          UserProfilePhotoRepository userProfilePhotoRepository,
                          UserAuthorityRepository userAuthorityRepository,
                          UserService userService,
                          MailService mailService,
                          AjaxLogoutSuccessHandler ajaxLogoutSuccessHandler,
                          HttpHeaderCreator httpHeaderCreator) {
        this.userRepository = userRepository;
        this.userProfilePhotoRepository = userProfilePhotoRepository;
        this.userAuthorityRepository = userAuthorityRepository;
        this.userService = userService;
        this.mailService = mailService;
        this.ajaxLogoutSuccessHandler = ajaxLogoutSuccessHandler;
        this.httpHeaderCreator = httpHeaderCreator;
    }

    @ApiOperation(value = "创建新用户并发送激活邮件", response = String.class)
    @ApiResponses(value = {@ApiResponse(code = SC_CREATED, message = "成功创建"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号已注册")})
    @PostMapping("/api/user/users")
    @Secured({Authority.ADMIN})
    public ResponseEntity<String> create(@ApiParam(value = "用户", required = true) @Valid @RequestBody UserDTO dto,
                                         HttpServletRequest request) {
        log.debug("REST request to create user: {}", dto);
        User newUser = userService.insert(dto.getUserName(), DEFAULT_PASSWORD, dto.getFirstName(), dto.getLastName(),
                dto.getEmail().toLowerCase(), dto.getMobileNo(), RandomUtils.generateActivationKey(),
                dto.getActivated(), dto.getEnabled(), dto.getRemarks(), RandomUtils.generateResetKey(),
                Instant.now(), dto.getAuthorities());
        String baseUrl = request.getScheme() + // "http"
                "://" + // "://"
                request.getServerName() + // "host"
                ":" + // ":"
                request.getServerPort() + // "80"
                request.getContextPath(); // "/myContextPath" or "" if
        // deployed in root context
        mailService.sendCreationEmail(newUser, baseUrl);
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("SM1001", dto.getUserName(),
                DEFAULT_PASSWORD);
        return ResponseEntity
                .status(HttpStatus.CREATED).headers(headers).body(DEFAULT_PASSWORD);
    }

    @ApiOperation("分页检索用户列表")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping("/api/user/users")
    @Secured({Authority.ADMIN})
    public ResponseEntity<List<ManagedUserDTO>> find(Pageable pageable,
                                                     @ApiParam(value = "检索条件") @RequestParam(value = "login", required = false) String login)
            throws URISyntaxException {
        Page<User> users = StringUtils.isEmpty(login) ? userRepository.findAll(pageable)
                : userService.findByLogin(pageable, login);
        List<ManagedUserDTO> DTOs = users.getContent().stream().map(entity -> new ManagedUserDTO(entity, null))
                .collect(Collectors.toList());
        HttpHeaders headers = generatePageHeaders(users);
        return ResponseEntity.ok().headers(headers).body(DTOs);
    }

    @ApiOperation("根据用户名检索用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户不存在或账号无权限")})
    @GetMapping("/api/user/users/{userName:[_'.@a-z0-9-]+}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<ManagedUserDTO> findByName(
            @ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        User entity = userService.findOneByUserName(userName).orElseThrow(() -> new NoDataFoundException(userName));
        List<UserAuthority> userAuthorities = Optional.ofNullable(userAuthorityRepository.findByUserId(entity.getId()))
                .orElseThrow(() -> new NoAuthorityException(userName));
        Set<String> authorities = userAuthorities.stream().map(UserAuthority::getAuthorityName)
                .collect(Collectors.toSet());
        return ResponseEntity.ok(new ManagedUserDTO(entity, authorities));
    }

    @ApiOperation("更新用户")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功更新"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户不存在"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "账号已注册"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户不存在"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "已激活用户无法变成未激活状态")})
    @PutMapping("/api/user/users")
    @Secured({Authority.ADMIN})
    public ResponseEntity<Void> update(@ApiParam(value = "新的用户", required = true) @Valid @RequestBody UserDTO dto,
                                       HttpServletRequest request, HttpServletResponse response) {
        log.debug("REST request to update user: {}", dto);
        userService.updateWithCheck(dto);

        if (dto.getUserName().equals(SecurityUtils.getCurrentUserName())) {
            // Remove access token from Redis if authorities of current user
            // were changed
            ajaxLogoutSuccessHandler.onLogoutSuccess(request, response, null);
        }
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1002", dto.getUserName())).build();
    }

    @ApiOperation(value = "根据用户名删除用户", notes = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功删除"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户不存在")})
    @DeleteMapping("/api/user/users/{userName:[_'.@a-z0-9-]+}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<Void> delete(@ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        log.debug("REST request to delete user: {}", userName);
        User user = userService.findOneByUserName(userName).orElseThrow(() -> new NoDataFoundException(userName));
        userRepository.deleteById(user.getId());
        userAuthorityRepository.deleteByUserId(user.getId());
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", userName)).build();
    }

    @ApiOperation("根据用户名重置密码")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功重置"),
            @ApiResponse(code = SC_BAD_REQUEST, message = "用户不存在或账号无权限")})
    @PutMapping("/api/user/users/{userName:[_'.@a-z0-9-]+}")
    @Secured({Authority.ADMIN})
    public ResponseEntity<String> resetPassword(@ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        log.debug("REST reset the password of user: {}", userName);
        userService.changePassword(UserNameAndPasswordDTO.builder().userName(userName).newPassword(DEFAULT_PASSWORD).build());
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2011", DEFAULT_PASSWORD);
        return ResponseEntity.ok().headers(headers).body(DEFAULT_PASSWORD);
    }

    public static final String GET_PROFILE_PHOTO_URL = "/api/user/profile-photo/";

    @ApiOperation("检索用户头像")
    @ApiResponses(value = {@ApiResponse(code = SC_OK, message = "成功检索")})
    @GetMapping(GET_PROFILE_PHOTO_URL + "{userName:[_'.@a-z0-9-]+}")
    @Secured({Authority.USER})
    public ResponseEntity<byte[]> getProfilePhoto(@ApiParam(value = "用户名", required = true) @PathVariable String userName) {
        Optional<UserProfilePhoto> userProfilePhoto = userProfilePhotoRepository.findByUserName(userName);
        return userProfilePhoto.map(photo -> ResponseEntity.ok(photo.getProfilePhoto().getData()))
                .orElse(null);
    }
}
