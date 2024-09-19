package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.event.LogoutEvent;
import cn.luixtech.passport.server.pojo.AuthUser;
import cn.luixtech.passport.server.pojo.ChangePassword;
import cn.luixtech.passport.server.pojo.ManagedUser;
import cn.luixtech.passport.server.pojo.PasswordRecovery;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.MailService;
import cn.luixtech.passport.server.service.UserProfilePicService;
import cn.luixtech.passport.server.service.UserService;
import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.springbootframework.component.MessageCreator;
import com.luixtech.utilities.encryption.JasyptEncryptUtils;
import com.luixtech.utilities.exception.DataNotFoundException;
import com.luixtech.utilities.lang.DateUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.lang3.Validate;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.core.io.ByteArrayResource;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Optional;

import static cn.luixtech.passport.server.domain.UserRole.*;
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;

/**
 * REST controller for managing the user's account.
 */
@RestController
@AllArgsConstructor
@Slf4j
public class AccountController {
    private final HttpHeaderCreator         httpHeaderCreator;
    private final MessageCreator            messageCreator;
    private final MailService               mailService;
    private final UserRepository            userRepository;
    private final UserProfilePicRepository  userProfilePicRepository;
    private final UserService               userService;
    private final UserProfilePicService     userProfilePicService;
    private final ApplicationEventPublisher applicationEventPublisher;

    @Operation(summary = "get current user who are signed in")
    @GetMapping("/open-api/accounts/user")
    public ResponseEntity<AuthUser> getCurrentUser() {
        return AuthUtils.getCurrentUserId() == null ?
                ResponseEntity.ok(null) :
                ResponseEntity.ok(AuthUser.of(userService.findById(AuthUtils.getCurrentUserId())));
    }

    @Operation(summary = "update current user")
    @PutMapping("/api/accounts/user")
    public ResponseEntity<Void> update(@Parameter(description = "new user info", required = true) @Valid @RequestBody User domain) {
        User existingOne = userRepository.findById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new DataNotFoundException(AuthUtils.getCurrentUserId()));
        Validate.isTrue(StringUtils.isNotEmpty(domain.getId()) && existingOne.getId().equals(domain.getId()), "Invalid user ID!");
        existingOne.setFirstName(domain.getFirstName());
        existingOne.setLastName(domain.getLastName());
        existingOne.setLocale(domain.getLocale());
        existingOne.setTimeZone(domain.getTimeZone());
        existingOne.setDateTimeFormat(domain.getDateTimeFormat());
        userService.update(existingOne);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUsername())).build();
    }

    @Operation(summary = "send password change verification code email")
    @PostMapping("/api/accounts/request-password-change-verification-code")
    public ResponseEntity<Void> requestPasswordChangeVerificationCode(HttpServletRequest request) {
        User currentUser = userRepository.findById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new DataNotFoundException(AuthUtils.getCurrentUserId()));
        User user = userService.requestPasswordChangeVerificationCode(currentUser);
        mailService.sendVerificationCodeMail(user, user.getEmail(), getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1002")).build();
    }

    @Operation(summary = "update password of the current user")
    @PutMapping("/api/accounts/password")
    public ResponseEntity<Void> changePassword(HttpServletRequest request,
                                               @Parameter(description = "new password", required = true) @Valid @RequestBody ChangePassword dto) {
        User user = userService.changePassword(AuthUtils.getCurrentUserId(), dto.getOldRawPassword(), dto.getNewRawPassword(), dto.getVerificationCode());
        mailService.sendPasswordChangedMail(user, getRequestUrl(request));
        // Logout asynchronously
        applicationEventPublisher.publishEvent(new LogoutEvent(this, AuthUtils.getCurrentUsername()));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", messageCreator.getMessage("password"))).build();
    }

    @Operation(summary = "register a new user and send an account activation email")
    @PostMapping("/open-api/accounts/register")
    public ResponseEntity<Void> register(HttpServletRequest request,
                                         @Parameter(description = "user", required = true) @Valid @RequestBody ManagedUser managedUser) {
        User newUser = userService.insert(managedUser.toUser(), managedUser.getRoles(), managedUser.getPassword(), false);
        mailService.sendAccountActivationEmail(newUser, getRequestUrl(request));
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("SM1021", newUser.getUsername());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
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
        User currentUser = userRepository.findById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new DataNotFoundException(AuthUtils.getCurrentUserId()));
        User user = userService.requestEmailChangeVerificationCode(currentUser, email);
        mailService.sendVerificationCodeMail(user, email, getRequestUrl(request));
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1002")).build();
    }

    @Operation(summary = "change email with verification code")
    @PostMapping("/api/accounts/change-email")
    public ResponseEntity<Void> changeEmail(@Parameter(description = "verificationCode", required = true) @RequestParam String verificationCode) {
        User currentUser = userRepository.findById(AuthUtils.getCurrentUserId()).orElseThrow(() -> new DataNotFoundException(AuthUtils.getCurrentUserId()));
        Validate.isTrue(StringUtils.isNotEmpty(currentUser.getVerificationCode()), "Please send verification code first!");
        Validate.isTrue(verificationCode.equalsIgnoreCase(currentUser.getVerificationCode()), "Invalid verification code!");
        Validate.isTrue(currentUser.getVerificationCodeSentAt().plusDays(1).isAfter(LocalDateTime.now()), "Invalid verification exceeds one day before!");
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
        String resetCode;
        try {
            resetCode = JasyptEncryptUtils.decrypt(dto.getResetCode());
        } catch (Exception ex) {
            throw new IllegalArgumentException(messageCreator.getMessage("IA2001"));
        }
        userService.resetPassword(resetCode, dto.getNewRawPassword());
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("NM1003")).build();
    }

    @Operation(summary = "get profile picture of the current user")
    @GetMapping("/api/accounts/profile-pic")
    public ResponseEntity<byte[]> getProfilePicture(HttpServletRequest request) throws IOException {
        Optional<UserProfilePic> userPhoto = userProfilePicRepository.findById(AuthUtils.getCurrentUserId());
        if (userPhoto.isPresent()) {
            return ResponseEntity.ok(userPhoto.get().getProfilePic());
        }
        // Set default profile picture
        byte[] bytes = StreamUtils.copyToByteArray(
                new UrlResource(getRequestUrl(request) + "/assets/images/cartoon/01.png").getInputStream());
        return ResponseEntity.ok(bytes);
    }

    @Operation(summary = "upload profile picture of the current user")
    @PostMapping(value = "/api/accounts/profile-pic/upload", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = MediaType.APPLICATION_JSON_VALUE)
    public void uploadProfilePicture(@Parameter(description = "user profile photo", required = true) @RequestPart MultipartFile file) throws IOException {
        User user = Optional.ofNullable(userService.findById(AuthUtils.getCurrentUserId())).orElseThrow(() -> new DataNotFoundException(AuthUtils.getCurrentUserId()));
        userProfilePicService.save(user, file.getBytes());
        log.info("Uploaded profile picture with file name {}", file.getOriginalFilename());
    }

    @Operation(summary = "download profile picture of the current user")
    @GetMapping("/api/accounts/profile-pic/download")
    public ResponseEntity<Resource> downloadProfilePicture() {
        Optional<UserProfilePic> existingOne = userProfilePicRepository.findById(AuthUtils.getCurrentUserId());
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

    @Operation(summary = "get all authority names")
    @GetMapping("/api/accounts/all-authorities")
    public ResponseEntity<List<String>> getAuthorityNames() {
        return ResponseEntity.ok(Arrays.asList(ROLE_ANONYMOUS, ROLE_USER, ROLE_ADMIN, ROLE_DEVELOPER));
    }

    @Operation(summary = "delete current user")
    @DeleteMapping("/api/accounts")
    public void delete() {
        userService.deleteById(AuthUtils.getCurrentUserId());
    }

    @Operation(summary = "sign out current user")
    @PostMapping("/api/accounts/sign-out")
    public void signOut(HttpServletRequest request) {
        request.getSession(false).invalidate();
    }
}
