package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.DataDict;
import cn.luixtech.passport.server.domain.SupportedDateTimeFormat;
import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.event.LogoutEvent;
import cn.luixtech.passport.server.pojo.*;
import cn.luixtech.passport.server.repository.SupportedDateTimeFormatRepository;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.DataDictService;
import cn.luixtech.passport.server.service.MailService;
import cn.luixtech.passport.server.service.UserProfilePicService;
import cn.luixtech.passport.server.service.UserService;
import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import com.luixtech.utilities.lang.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static cn.luixtech.passport.server.service.impl.UserProfilePicServiceImpl.CURRENT_USER_PHOTO_URL;
import static cn.luixtech.passport.server.utils.ImageCompressor.compressImage;
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;

/**
 * REST controller for managing the user's account.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class AccountController {
    private final HttpHeaderCreator                 httpHeaderCreator;
    private final MessageCreator                    messageCreator;
    private final MailService                       mailService;
    private final UserRepository                    userRepository;
    private final UserProfilePicRepository          userProfilePicRepository;
    private final SupportedDateTimeFormatRepository supportedDateTimeFormatRepository;
    private final UserService                       userService;
    private final UserProfilePicService             userProfilePicService;
    private final DataDictService                   dataDictService;
    private final ApplicationEventPublisher         applicationEventPublisher;
    private final AuthenticationManager             authenticationManager;

    @Operation(summary = "get current user who are signed in")
    @GetMapping("/open-api/accounts/user")
    public ResponseEntity<LoginUser> getCurrentUser() {
        if (AuthUtils.getCurrentUser() == null || AuthUtils.getCurrentUser().getEmail() == null) {
            return ResponseEntity.ok(null);
        }
        LoginUser loginUser = LoginUser.of(userService.findByEmail(AuthUtils.getCurrentUser().getEmail()));
        SupportedDateTimeFormat supportedDateTimeFormat =
                supportedDateTimeFormatRepository.findById(loginUser.getDateTimeFormatId())
                        .orElseThrow(() -> new DataNotFoundException(loginUser.getDateTimeFormatId()));
        loginUser.setDateTimeFormat(supportedDateTimeFormat.getDateTimeFormat());
        loginUser.setDateFormat(supportedDateTimeFormat.getDateFormat());
        loginUser.setTimeFormat(supportedDateTimeFormat.getTimeFormat());
        return ResponseEntity.ok(loginUser);
    }

    @Operation(summary = "sign in for current system")
    @PostMapping("/open-api/accounts/sign-in")
    public ResponseEntity<Void> signIn(HttpServletRequest request,
                                       @Valid @RequestBody LoginRequest loginRequest) {
        // create an authentication token
        UsernamePasswordAuthenticationToken authRequest = new UsernamePasswordAuthenticationToken(
                loginRequest.getUsername(),
                loginRequest.getPassword()
        );

        // authenticate
        Authentication authentication = authenticationManager.authenticate(authRequest);

        // save the result of authentication to security context
        SecurityContextHolder.getContext().setAuthentication(authentication);

        // create a new session
        HttpSession session = request.getSession(true);
        session.setAttribute("SPRING_SECURITY_CONTEXT", SecurityContextHolder.getContext());

        return ResponseEntity.ok().build();
    }

    @Operation(summary = "sign out current user")
    @PostMapping("/api/accounts/sign-out")
    public void signOut(HttpServletRequest request,
                        HttpServletResponse response) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null) {
            new SecurityContextLogoutHandler().logout(request, response, auth);
        }
    }

    @Operation(summary = "register a new user and send an account activation email")
    @PostMapping("/open-api/accounts/register")
    public ResponseEntity<Void> register(HttpServletRequest request,
                                         @Parameter(description = "user", required = true) @Valid @RequestBody ManagedUser managedUser) {
        if (userRepository.findOneByEmail(managedUser.getEmail()).isPresent()) {
            throw new RuntimeException("This email is already registered. Please use a different one!");
        }
        if (userRepository.findById(managedUser.getUsername().toLowerCase()).isPresent()) {
            throw new RuntimeException("This username is already registered. Please use a different one!");
        }

        User newUser = userService.insert(managedUser.toUser(), managedUser.getRoleIds(), managedUser.getPassword(), false);
        mailService.sendAccountActivationEmail(newUser, getRequestUrl(request));
        return ResponseEntity.ok().build();
    }

    @Operation(summary = "update current user")
    @PutMapping("/api/accounts/user")
    public ResponseEntity<Void> update(@Parameter(description = "new user info", required = true) @Valid @RequestBody ManagedUser domain) {
        Validate.isTrue(AuthUtils.getCurrentUsername().equals(domain.getUsername()), "Invalid username!");
        domain.setEmail(null);
        domain.setMobileNo(null);
        domain.setRemark(null);
        domain.setRoleIds(null);
        domain.setEnabled(null);
        userService.update(domain);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUsername())).build();
    }

    @Operation(summary = "delete current user")
    @DeleteMapping("/api/accounts")
    public void delete() {
        userService.deleteByUsername(AuthUtils.getCurrentUsername());
    }

    @Operation(summary = "send password change verification code email")
    @PostMapping("/api/accounts/request-password-change-verification-code")
    public ResponseEntity<Void> requestPasswordChangeVerificationCode(HttpServletRequest request) {
        User currentUser = userService.getCurrentUser();
        User user = userService.requestPasswordChangeVerificationCode(currentUser);
        mailService.sendVerificationCodeMail(user, user.getEmail(), getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1002")).build();
    }

    @Operation(summary = "update password of the current user")
    @PutMapping("/api/accounts/password")
    public ResponseEntity<Void> changePassword(HttpServletRequest request,
                                               @Parameter(description = "new password", required = true) @Valid @RequestBody ChangePassword dto) {
        User user = userService.changePassword(AuthUtils.getCurrentUsername(), dto.getOldRawPassword(), dto.getNewRawPassword(), dto.getVerificationCode());
        mailService.sendPasswordChangedMail(user, getRequestUrl(request));
        // Logout asynchronously
        applicationEventPublisher.publishEvent(new LogoutEvent(this, AuthUtils.getCurrentUsername()));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", messageCreator.getMessage("password"))).build();
    }

    @Operation(summary = "activate the account by activation code")
    @GetMapping("/open-api/accounts/activate/{code}")
    public void activate(@Parameter(description = "activation code", required = true) @PathVariable String code) {
        userService.activate(code);
    }

    @Operation(summary = "send email change verification code email")
    @PostMapping("/api/accounts/request-email-change-verification-code")
    public ResponseEntity<Void> requestEmailChangeVerificationCode(HttpServletRequest request,
                                                                   @Parameter(description = "email", required = true) @RequestParam String email) {
        User currentUser = userService.getCurrentUser();
        User user = userService.requestEmailChangeVerificationCode(currentUser, email);
        mailService.sendVerificationCodeMail(user, email, getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1002")).build();
    }

    @Operation(summary = "change email with verification code")
    @PostMapping("/api/accounts/change-email")
    public ResponseEntity<Void> changeEmail(@Parameter(description = "verificationCode", required = true) @RequestParam String verificationCode) {
        User currentUser = userService.getCurrentUser();
        Validate.isTrue(StringUtils.isNotEmpty(currentUser.getVerificationCode()), "Please send verification code first!");
        Validate.isTrue(verificationCode.equalsIgnoreCase(currentUser.getVerificationCode()), "Invalid verification code!");
        Validate.isTrue(currentUser.getVerificationCodeSentAt().plus(1, ChronoUnit.DAYS).isAfter(Instant.now()), "Invalid verification exceeds one day before!");
        userService.changeToNewEmail(currentUser);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002")).build();
    }

    @Operation(summary = "send password recovery email")
    @PostMapping("/open-api/accounts/request-password-recovery")
    public ResponseEntity<Void> requestRecoverPassword(HttpServletRequest request,
                                                       @Parameter(description = "email", required = true) @RequestParam String email) {
        User user = userService.requestPasswordRecovery(email);
        mailService.sendPasswordRecoveryMail(user, getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1002")).build();
    }

    @Operation(summary = "complete password recovery")
    @PostMapping("/open-api/accounts/complete-password-recovery")
    public ResponseEntity<Void> completeRecoverPassword(@Parameter(description = "reset code and new password", required = true) @Valid @RequestBody PasswordRecovery dto) {
        userService.resetPassword(dto.getResetCode(), dto.getNewRawPassword());
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1003")).build();
    }

    @Operation(summary = "get profile picture of the current user")
    @GetMapping(CURRENT_USER_PHOTO_URL)
    public ResponseEntity<byte[]> getProfilePicture(HttpServletRequest request) throws IOException {
        return ResponseEntity.ok(userProfilePicService.getProfilePic(AuthUtils.getCurrentUsername(), request));
    }

    @Operation(summary = "upload profile picture of the current user")
    @PostMapping(value = "/api/accounts/profile-pic/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadProfilePicture(@Parameter(description = "user profile photo", required = true) @RequestPart MultipartFile file) throws IOException {
        log.info("Original file size: {} bytes ({} KB)", file.getSize(), file.getSize() / 1024);
        byte[] compressedImage = compressImage(file);
        log.info("Compressed file size: {} bytes ({} KB)", compressedImage.length, compressedImage.length / 1024);
        userProfilePicService.save(userService.getCurrentUser().getUsername(), compressedImage);
        log.info("Uploaded profile picture with file name {}", file.getOriginalFilename());
    }

    @Operation(summary = "download profile picture of the current user")
    @GetMapping("/api/accounts/profile-pic/download")
    public ResponseEntity<Resource> downloadProfilePicture() {
        Optional<UserProfilePic> existingOne = userProfilePicRepository.findById(AuthUtils.getCurrentUsername());
        if (existingOne.isEmpty()) {
            return ResponseEntity.ok().body(null);
        }
        ByteArrayResource resource = new ByteArrayResource(existingOne.get().getProfilePic());
        String fileName = "pic-" + DateUtils.FILE_DATETIME_FORMAT.format(new Date()) + ".jpg";
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment;filename=" + fileName)
                .contentType(MediaType.APPLICATION_OCTET_STREAM)
                .contentLength(existingOne.get().getProfilePic().length)
                .body(resource);

//        String path = System.getProperty("user.home") + File.separator + "fileName.txt";
//        File outFile = ResourceUtils.getFile(path);
//        FileUtils.writeLines(outFile, strList);
    }

    @Operation(summary = "get all supported time zones")
    @GetMapping("/api/accounts/all-supported-time-zones")
    public ResponseEntity<List<DataDict>> getSupportedTimeZones() {
        return ResponseEntity.ok(dataDictService.findAllTimezone());
    }

    @Operation(summary = "get all supported date time formats")
    @GetMapping("/api/accounts/all-supported-date-time-formats")
    public ResponseEntity<List<SupportedDateTimeFormat>> getSupportedDateTimeFormats() {
        return ResponseEntity.ok(supportedDateTimeFormatRepository.findAll());
    }
}
