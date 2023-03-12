package com.luixtech.passport.controller;

import com.luixtech.framework.component.HttpHeaderCreator;
import com.luixtech.passport.config.ApplicationProperties;
import com.luixtech.passport.config.oauth2.LogoutEvent;
import com.luixtech.passport.config.oauth2.SecurityUtils;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.User;
import com.luixtech.passport.domain.UserProfilePhoto;
import com.luixtech.passport.dto.ManagedUserDTO;
import com.luixtech.passport.dto.UsernameAndPasswordDTO;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.UserProfilePhotoRepository;
import com.luixtech.passport.repository.UserRepository;
import com.luixtech.passport.service.MailService;
import com.luixtech.passport.service.UserService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import javax.validation.Valid;
import java.util.List;
import java.util.Optional;

import static com.luixtech.framework.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.framework.utils.NetworkUtils.getRequestUrl;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing users.
 */
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
@Slf4j
public class UserController {
    public static final String                     GET_PROFILE_PHOTO_URL = "/api/users/profile-photo/";
    private final       ApplicationProperties      applicationProperties;
    private final       UserProfilePhotoRepository userProfilePhotoRepository;
    private final       UserRepository             userRepository;
    private final       UserService                userService;
    private final       MailService                mailService;
    private final ApplicationEventPublisher applicationEventPublisher;
    private final HttpHeaderCreator         httpHeaderCreator;

    @Operation(summary = "create new user and send activation email")
    @PostMapping("/api/users")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(@Parameter(description = "user", required = true) @Valid @RequestBody User domain,
                                       HttpServletRequest request) {
        log.debug("REST request to create user: {}", domain);
        User newUser = userService.insert(domain, applicationProperties.getAccount().getDefaultPassword());
        mailService.sendCreationEmail(newUser, getRequestUrl(request));
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2011", applicationProperties.getAccount().getDefaultPassword());
        return ResponseEntity.status(HttpStatus.CREATED).headers(headers).build();
    }

    @Operation(summary = "find user list")
    @GetMapping("/api/users")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<User>> find(@ParameterObject Pageable pageable,
                                           @Parameter(description = "username/email/mobile") @RequestParam(value = "login", required = false) String login) {
        Page<User> domains = userService.findByLogin(pageable, login);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find user by id")
    @GetMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<ManagedUserDTO> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        User user = userRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(new ManagedUserDTO(user));
    }

    @Operation(summary = "update user")
    @PutMapping("/api/users")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "new user", required = true) @Valid @RequestBody User domain) {
        log.debug("REST request to update user: {}", domain);
        userService.update(domain);
        if (domain.getUsername().equals(SecurityUtils.getCurrentUsername())) {
            // Logout if current user were changed
            applicationEventPublisher.publishEvent(new LogoutEvent(this));
        }
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getUsername())).build();
    }

    @Operation(summary = "delete user by id", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/users/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete user: {}", id);
        userRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }

    @Operation(summary = "reset password")
    @PutMapping("/api/users/{username:[a-zA-Z0-9-]+}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> resetPassword(@Parameter(description = "username", required = true) @PathVariable String username) {
        log.debug("REST reset the password of user: {}", username);
        UsernameAndPasswordDTO dto = UsernameAndPasswordDTO.builder()
                .username(username)
                .newPassword(applicationProperties.getAccount().getDefaultPassword()).build();
        userService.changePassword(dto);
        HttpHeaders headers = httpHeaderCreator.createSuccessHeader("NM2012", applicationProperties.getAccount().getDefaultPassword());
        return ResponseEntity.ok().headers(headers).build();
    }

    @Operation(summary = "get user profile photo")
    @GetMapping(GET_PROFILE_PHOTO_URL + "{username:[a-zA-Z0-9-]+}")
    @PreAuthorize("hasAuthority(\"" + Authority.USER + "\")")
    public ResponseEntity<byte[]> getProfilePhoto(@Parameter(description = "username", required = true) @PathVariable String username) {
        User user = userService.findOneByUsername(username);
        Optional<UserProfilePhoto> userProfilePhoto = userProfilePhotoRepository.findByUserId(user.getId());
        return userProfilePhoto.map(photo -> ResponseEntity.ok(photo.getProfilePhoto())).orElse(null);
    }
}
