package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserAuthEvent;
import cn.luixtech.passport.server.domain.UserProfilePic;
import cn.luixtech.passport.server.pojo.LoginUser;
import cn.luixtech.passport.server.repository.UserAuthEventRepository;
import cn.luixtech.passport.server.repository.UserProfilePicRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserAuthEventService;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.core.io.UrlResource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.luixtech.passport.server.controller.UserProfilePicController.DEFAULT_USER_PHOTO_URL;
import static cn.luixtech.passport.server.domain.UserAuthEvent.AUTH_SUCCESS;
import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;
import static com.luixtech.springbootframework.utils.NetworkUtils.getRequestUrl;

/**
 * REST controller for managing user authentication events.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class UserAuthEventController {
    private final UserAuthEventRepository  userAuthEventRepository;
    private final UserRepository           userRepository;
    private final UserProfilePicRepository userProfilePicRepository;
    private final UserAuthEventService     userAuthEventService;
    private final HttpHeaderCreator        httpHeaderCreator;


    @Operation(summary = "find user auth event list")
    @GetMapping("/api/user-auth-events")
    public ResponseEntity<List<UserAuthEvent>> find(@ParameterObject Pageable pageable,
                                                    @Parameter(description = "userId") @RequestParam(value = "userId", required = false) String userId,
                                                    @Parameter(description = "event") @RequestParam(value = "event", required = false) String event) {
        Page<UserAuthEvent> domains = userAuthEventService.find(pageable, userId, event);
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(domains.getContent());
    }

    @Operation(summary = "find user auth event by id")
    @GetMapping("/api/user-auth-events/{id}")
    public ResponseEntity<UserAuthEvent> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        UserAuthEvent domain = userAuthEventRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "find recent login users list")
    @GetMapping("/api/user-auth-events/recent-login-users")
    public ResponseEntity<List<LoginUser>> findRecentLoginUsers(HttpServletRequest request,
                                                                @ParameterObject Pageable pageable) throws IOException {
        List<LoginUser> loginUsers = new ArrayList<>();
        Page<UserAuthEvent> domains = userAuthEventService.find(pageable, null, AUTH_SUCCESS);
        if (domains.isEmpty()) {
            return ResponseEntity.ok(loginUsers);
        }
        for (UserAuthEvent domain : domains.getContent()) {
            Optional<User> user = userRepository.findById(domain.getUserId());
            if (user.isPresent()) {
                LoginUser loginUser = new LoginUser();
                BeanUtils.copyProperties(user, loginUser);
                Optional<UserProfilePic> userProfilePic = userProfilePicRepository.findById(user.get().getId());
                if (userProfilePic.isPresent()) {
                    loginUser.setProfilePic(userProfilePic.get().getProfilePic());
                } else {
                    loginUser.setProfilePic(StreamUtils.copyToByteArray(
                            new UrlResource(getRequestUrl(request) + DEFAULT_USER_PHOTO_URL).getInputStream()));
                }
                loginUsers.add(loginUser);
            }
        }
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(loginUsers);
    }

    @Operation(summary = "delete user auth event by id", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/user-auth-events/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        userAuthEventRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
