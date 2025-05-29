package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.service.UserProfilePicService;
import com.luixtech.utilities.encryption.JasyptEncryptUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.utilities.encryption.JasyptEncryptUtils.DEFAULT_ALGORITHM;

/**
 * REST controller for managing users' profile picture.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class UserProfilePicController {
    public static final String                USER_PHOTO_TOKEN_KEY   = "dw4rfer54g&^@dsfd#";
    public static final String                USER_PHOTO_URL         = "/open-api/user-profile-pics/";
    public static final String                DEFAULT_USER_PHOTO_URL = "/assets/images/cartoon/01.png";
    private final       UserProfilePicService userProfilePicService;

    @Operation(summary = "find user profile picture by user id")
    @GetMapping("/api/user-profile-pics/{username}")
    public ResponseEntity<byte[]> findById(HttpServletRequest request,
                                           @Parameter(description = "username", required = true) @PathVariable String username) throws IOException {
        return ResponseEntity.ok(userProfilePicService.getProfilePic(username, request));
    }

    @Operation(summary = "find user profile picture by user token")
    @GetMapping(USER_PHOTO_URL + "{userToken}")
    public ResponseEntity<byte[]> findByUserToken(HttpServletRequest request,
                                                  @Parameter(description = "userToken", required = true) @PathVariable String userToken) throws IOException {
        String username = JasyptEncryptUtils.decrypt(userToken, DEFAULT_ALGORITHM, USER_PHOTO_TOKEN_KEY);
        return findById(request, username);
    }
}
