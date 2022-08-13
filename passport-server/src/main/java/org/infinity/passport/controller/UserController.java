package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.config.ApplicationProperties;
import org.infinity.passport.domain.Authority;
import org.infinity.passport.domain.User;
import org.infinity.passport.domain.UserAuthority;
import org.infinity.passport.domain.UserProfilePhoto;
import org.infinity.passport.dto.ManagedUserDTO;
import org.infinity.passport.dto.UserNameAndPasswordDTO;
import org.infinity.passport.event.LogoutEvent;
import org.infinity.passport.exception.NoAuthorityException;
import org.infinity.passport.repository.UserAuthorityRepository;
import org.infinity.passport.repository.UserProfilePhotoRepository;
import org.infinity.passport.service.MailService;
import org.infinity.passport.service.UserService;
import org.infinity.passport.config.oauth2.SecurityUtils;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;
import static org.infinity.passport.utils.HttpHeaderUtils.generatePageHeaders;
import static org.infinity.passport.utils.NetworkUtils.getRequestUrl;

/**
 * REST controller for managing users.
 */
@RestController
@Tag(name = "用户管理")
@SecurityRequirement(name = AUTH)
@Slf4j
public class UserController {

    @Resource
    private ApplicationProperties      applicationProperties;
    @Resource
    private UserProfilePhotoRepository userProfilePhotoRepository;
    @Resource
    private UserAuthorityRepository    userAuthorityRepository;
    @Resource
    private UserService                userService;
    @Resource
    private MailService                mailService;
    @Resource
    private ApplicationEventPublisher  applicationEventPublisher;
    @Resource
    private HttpHeaderCreator          httpHeaderCreator;

    @Operation(summary = "创建新用户并发送激活邮件")
    @PostMapping("/api/users")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(@Parameter(description = "用户", required = true) @Valid @RequestBody User domain,
                                       HttpServletRequest request) {
        log.debug("REST request to create user: {}", domain);
        User newUser = userService.insert(domain, applicationProperties.getAccount().getDefaultPassword());
        mailService.sendCreationEmail(newUser, getRequestUrl(request));
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2011", applicationProperties.getAccount().getDefaultPassword());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }

    @Operation(summary = "分页检索用户列表")
    @GetMapping("/api/users")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<User>> find(@ParameterObject Pageable pageable,
                                           @Parameter(description = "检索条件") @RequestParam(value = "login", required = false) String login) {
        Page<User> users = userService.findByLogin(pageable, login);
        return ResponseEntity.ok().headers(generatePageHeaders(users)).body(users.getContent());
    }

    @Operation(summary = "根据用户名检索用户")
    @GetMapping("/api/users/{username:[a-zA-Z0-9-]+}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<ManagedUserDTO> findByName(@Parameter(description = "用户名", required = true) @PathVariable String username) {
        User domain = userService.findOneByUsername(username);
        List<UserAuthority> userAuthorities = Optional.ofNullable(userAuthorityRepository.findByUserId(domain.getId()))
                .orElseThrow(() -> new NoAuthorityException(username));
        Set<String> authorities = userAuthorities.stream().map(UserAuthority::getAuthorityName).collect(Collectors.toSet());
        return ResponseEntity.ok(new ManagedUserDTO(domain, authorities));
    }

    @Operation(summary = "更新用户")
    @PutMapping("/api/users")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "新的用户", required = true) @Valid @RequestBody User domain) {
        log.debug("REST request to update user: {}", domain);
        userService.update(domain);
        if (domain.getUsername().equals(SecurityUtils.getCurrentUsername())) {
            // Logout if current user were changed
            applicationEventPublisher.publishEvent(new LogoutEvent(this));
        }
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUsername())).build();
    }

    @Operation(summary = "根据用户名删除用户", description = "数据有可能被其他数据所引用，删除之后可能出现一些问题")
    @DeleteMapping("/api/users/{username:[a-zA-Z0-9-]+}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "用户名", required = true) @PathVariable String username) {
        log.debug("REST request to delete user: {}", username);
        userService.deleteByUsername(username);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", username)).build();
    }

    @Operation(summary = "根据用户名重置密码")
    @PutMapping("/api/users/{username:[a-zA-Z0-9-]+}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> resetPassword(@Parameter(description = "用户名", required = true) @PathVariable String username) {
        log.debug("REST reset the password of user: {}", username);
        UserNameAndPasswordDTO dto = UserNameAndPasswordDTO.builder()
                .username(username)
                .newPassword(applicationProperties.getAccount().getDefaultPassword()).build();
        userService.changePassword(dto);
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2012", applicationProperties.getAccount().getDefaultPassword());
        return ResponseEntity.ok().headers(headers).build();
    }

    public static final String GET_PROFILE_PHOTO_URL = "/api/users/profile-photo/";

    @Operation(summary = "检索用户头像")
    @GetMapping(GET_PROFILE_PHOTO_URL + "{username:[a-zA-Z0-9-]+}")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<byte[]> getProfilePhoto(@Parameter(description = "用户名", required = true) @PathVariable String username) {
        User user = userService.findOneByUsername(username);
        Optional<UserProfilePhoto> userProfilePhoto = userProfilePhotoRepository.findByUserId(user.getId());
        return userProfilePhoto.map(photo -> ResponseEntity.ok(photo.getProfilePhoto().getData())).orElse(null);
    }
}
