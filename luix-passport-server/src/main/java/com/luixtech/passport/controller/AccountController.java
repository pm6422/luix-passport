package com.luixtech.passport.controller;

import com.codahale.metrics.annotation.Timed;
import com.google.common.collect.ImmutableMap;
import com.luixtech.framework.component.HttpHeaderCreator;
import com.luixtech.passport.config.oauth2.LogoutEvent;
import com.luixtech.passport.config.oauth2.SecurityUser;
import com.luixtech.passport.config.oauth2.SecurityUtils;
import com.luixtech.passport.config.oauth2.service.SecurityUserService;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.domain.UserProfilePhoto;
import com.luixtech.passport.dto.ManagedUserDTO;
import com.luixtech.passport.dto.ResetKeyAndPasswordDTO;
import com.luixtech.passport.dto.UsernameAndPasswordDTO;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.UserProfilePhotoRepository;
import com.luixtech.passport.service.AuthorityService;
import com.luixtech.passport.service.MailService;
import com.luixtech.passport.service.UserProfilePhotoService;
import com.luixtech.passport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.time.FastDateFormat;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.ModelAndView;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.io.IOException;
import java.util.Date;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.stream.Collectors;

import static com.luixtech.framework.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.framework.utils.NetworkUtils.getRequestUrl;


/**
 * REST controller for managing the user's account.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class AccountController {
    private static final FastDateFormat             DATETIME_FORMAT = FastDateFormat.getInstance("yyyyMMdd-HHmmss");
    private final        UserService                userService;
    private final        UserProfilePhotoRepository userProfilePhotoRepository;
    private final        UserProfilePhotoService    userProfilePhotoService;
    private final        AuthorityService           authorityService;
    private final        MailService                mailService;
    private final        UserDetailsService         userDetailsService;
    private final        SecurityUserService        securityUserService;
    private final        ApplicationEventPublisher  applicationEventPublisher;
    private final        HttpHeaderCreator          httpHeaderCreator;

    @Operation(summary = "get access token from request")
    @GetMapping("/api/accounts/access-token")
    @Timed
    public ResponseEntity<String> getAccessToken(HttpServletRequest request) {
        return ResponseEntity.ok(SecurityUtils.getAccessToken(request));
    }

    @Operation(summary = "get current user")
    @GetMapping("/api/accounts/user")
    @Timed
    public ResponseEntity<User> getCurrentUser() {
        User user = userService.findOneByUsername(SecurityUtils.getCurrentUsername());
        HttpHeaders headers = new HttpHeaders();
        headers.add("X-User-Signed-In", "true");
        return ResponseEntity.ok().headers(headers).body(user);
    }

    @Operation(summary = "get user based on access token")
    @GetMapping("/open-api/accounts/user")
    @Timed
    public ResponseEntity<Object> getTokenUser(HttpServletRequest request) {
        SecurityUser securityUser = securityUserService.getUserByAccessToken(request);
        return ResponseEntity.ok(Objects.requireNonNullElseGet(securityUser, () -> ImmutableMap.of("error", true)));
    }

    @Operation(summary = "register a new user and send an activation email")
    @PostMapping("/open-api/accounts/register")
    @Timed
    public ResponseEntity<Void> registerAccount(
            @Parameter(description = "user", required = true) @Valid @RequestBody ManagedUserDTO dto,
            HttpServletRequest request) {
        log.debug("REST request to register user: {}", dto);
        User newUser = userService.insert(dto.toUser(), dto.getPassword());
        mailService.sendActivationEmail(newUser, getRequestUrl(request));
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2001");
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }

    @Operation(summary = "activate the account according to the activation code")
    @GetMapping("/open-api/accounts/activate/{code:[0-9]+}")
    @Timed
    public void activateAccount(@Parameter(description = "activation code", required = true) @PathVariable String code) {
        userService.activateRegistration(code).orElseThrow(() -> new DataNotFoundException(code));
    }

    @Operation(summary = "get authority name list")
    @GetMapping("/api/accounts/authority-names")
    @Timed
    public ResponseEntity<List<String>> getAuthorityNames(
            @Parameter(schema = @Schema(allowableValues = {"false", "true", "null"})) @RequestParam(value = "enabled", required = false) Boolean enabled) {
        List<String> authorities = authorityService.find(enabled).stream().map(Authority::getName).collect(Collectors.toList());
        return ResponseEntity.ok(authorities);
    }

    @Operation(summary = "update current user")
    @PutMapping("/api/accounts/user")
    @Timed
    public ResponseEntity<Void> updateCurrentAccount(@Parameter(description = "new user", required = true) @Valid @RequestBody User domain) {
        // For security reason
        User currentUser = userService.findOneByUsername(SecurityUtils.getCurrentUsername());
        domain.setId(currentUser.getId());
        domain.setUsername(currentUser.getUsername());
        domain.setEnabled(currentUser.getEnabled());
        domain.setActivated(currentUser.getActivated());
        domain.setProfilePhotoEnabled(currentUser.getProfilePhotoEnabled());
        domain.setAuthorities(currentUser.getAuthorities());

        userService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUsername())).build();
    }

    @Operation(summary = "modify the password of the current user")
    @PutMapping("/api/accounts/password")
    @Timed
    public ResponseEntity<Void> changePassword(@Parameter(description = "new password", required = true) @RequestBody @Valid UsernameAndPasswordDTO dto) {
        // For security reason
        dto.setUsername(SecurityUtils.getCurrentUsername());
        userService.changePassword(dto);
        // Logout asynchronously
        applicationEventPublisher.publishEvent(new LogoutEvent(this));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", "password")).build();
    }

    @Operation(summary = "send reset password email")
    @PostMapping("/open-api/accounts/reset-password/init")
    @Timed
    public ResponseEntity<Void> requestPasswordReset(@Parameter(description = "email", required = true) @RequestBody String email,
                                                     HttpServletRequest request) {
        User user = userService.requestPasswordReset(email, RandomStringUtils.randomNumeric(20));
        mailService.sendPasswordResetMail(user, getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM2002")).build();
    }

    @Operation(summary = "reset password")
    @PostMapping("/open-api/accounts/reset-password/finish")
    @Timed
    public ResponseEntity<Void> finishPasswordReset(@Parameter(description = "reset code and new password", required = true) @Valid @RequestBody ResetKeyAndPasswordDTO dto) {
        userService.completePasswordReset(dto.getNewPassword(), dto.getKey());
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM2003")).build();
    }

    @Operation(summary = "upload current user profile picture")
    @PostMapping("/api/accounts/profile-photo/upload")
    @Timed
    public void uploadProfilePhoto(@Parameter(description = "file description", required = true) @RequestPart String description,
                                   @Parameter(description = "user profile picture", required = true) @RequestPart MultipartFile file) throws IOException {
        User user = userService.findOneByUsername(SecurityUtils.getCurrentUsername());
        userProfilePhotoService.save(user, file.getBytes());
        log.info("Uploaded profile with file name {} and description {}", file.getOriginalFilename(), description);
    }

    @Operation(summary = "download user profile picture")
    @GetMapping("/api/accounts/profile-photo/download")
    @Timed
    public ResponseEntity<org.springframework.core.io.Resource> downloadProfilePhoto() {
        SecurityUser userDetails = (SecurityUser) userDetailsService.loadUserByUsername(SecurityUtils.getCurrentUsername());
        Optional<UserProfilePhoto> existingPhoto = userProfilePhotoRepository.findByUserId(userDetails.getId());
        if (existingPhoto.isEmpty()) {
            return ResponseEntity.ok().body(null);
        }
        ByteArrayResource resource = new ByteArrayResource(existingPhoto.get().getProfilePhoto());
        String fileName = "profile-" + DATETIME_FORMAT.format(new Date()) + ".jpg";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(existingPhoto.get().getProfilePhoto().length)
                .body(resource);

//        String path = System.getProperty("user.home") + File.separator + "fileName.txt";
//        File outFile = ResourceUtils.getFile(path);
//        FileUtils.writeLines(outFile, strList);
    }

    @Operation(summary = "get the current user avatar")
    @GetMapping("/api/accounts/profile-photo")
    @Timed
    public ModelAndView getProfilePhoto() {
        // @RestController下使用return forwardUrl不好使
        String forwardUrl = "forward:".concat(UserController.GET_PROFILE_PHOTO_URL).concat(SecurityUtils.getCurrentUsername());
        log.info(forwardUrl);
        return new ModelAndView(forwardUrl);
    }
}
