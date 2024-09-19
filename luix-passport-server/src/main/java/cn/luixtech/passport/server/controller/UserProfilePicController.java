package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import com.luixtech.utilities.encryption.JasyptEncryptUtils;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.io.UrlResource;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import java.io.IOException;
import java.util.Optional;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;
import static com.luixtech.utilities.encryption.JasyptEncryptUtils.DEFAULT_ALGORITHM;

/**
 * REST controller for managing users profile picture.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class UserProfilePicController {
    public static final String                   USER_PHOTO_TOKEN_KEY = "dw4rfer54g&^@dsfd#";
    public static final String                   USER_PHOTO_URL       = "/open-api/user-profile-pics/";
    private final       UserProfilePicRepository userProfilePicRepository;

    @Operation(summary = "find user profile picture by user id")
    @GetMapping("/api/user-profile-pics/{userId}")
    public ResponseEntity<byte[]> findById(HttpServletRequest request,
                                           @Parameter(description = "userId", required = true) @PathVariable String userId) throws IOException {
        Optional<UserProfilePic> userPhoto = userProfilePicRepository.findById(userId);
        if (userPhoto.isPresent()) {
            return ResponseEntity.ok(userPhoto.get().getProfilePic());
        }
        // Set default profile picture
        byte[] bytes = StreamUtils.copyToByteArray(
                new UrlResource(getRequestUrl(request) + "/assets/images/cartoon/01.png").getInputStream());
        return ResponseEntity.ok(bytes);
    }

    @Operation(summary = "find user profile picture by user token")
    @GetMapping(USER_PHOTO_URL + "{userToken}")
    public ResponseEntity<byte[]> findByUserToken(HttpServletRequest request,
                                                  @Parameter(description = "userToken", required = true) @PathVariable String userToken) throws IOException {
        String userId = JasyptEncryptUtils.decrypt(userToken, DEFAULT_ALGORITHM, USER_PHOTO_TOKEN_KEY);
        return findById(request, userId);
    }
}
