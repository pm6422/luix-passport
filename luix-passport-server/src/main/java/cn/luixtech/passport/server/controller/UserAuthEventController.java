package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.User;
import cn.luixtech.passport.server.domain.UserAuthEvent;
import cn.luixtech.passport.server.pojo.AuthUser;
import cn.luixtech.passport.server.pojo.ManagedUser;
import cn.luixtech.passport.server.pojo.UserLoginCount;
import cn.luixtech.passport.server.repository.UserAuthEventRepository;
import cn.luixtech.passport.server.repository.UserRepository;
import cn.luixtech.passport.server.service.UserAuthEventService;
import cn.luixtech.passport.server.service.UserService;
import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.time.Instant;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static cn.luixtech.passport.server.domain.UserAuthEvent.AUTH_SUCCESS;
import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;

/**
 * REST controller for managing user authentication events.
 */
@RestController
@AllArgsConstructor
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@Slf4j
public class UserAuthEventController {
    private final UserAuthEventRepository userAuthEventRepository;
    private final UserRepository          userRepository;
    private final UserAuthEventService    userAuthEventService;
    private final UserService             userService;
    private final HttpHeaderCreator       httpHeaderCreator;


    @Operation(summary = "find user auth event list")
    @GetMapping("/api/user-auth-events")
    public ResponseEntity<List<UserAuthEvent>> find(@ParameterObject Pageable pageable,
                                                    @Parameter(description = "username") @RequestParam(value = "username", required = false) String username,
                                                    @Parameter(description = "event") @RequestParam(value = "event", required = false) String event) {
        Page<UserAuthEvent> domains = userAuthEventService.find(pageable, username, event);
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
    public ResponseEntity<List<AuthUser>> findRecentLoginUsers(@ParameterObject Pageable pageable) {
        List<AuthUser> loginUsers = new ArrayList<>();
        Page<UserAuthEvent> domains = userAuthEventService.find(pageable, null, AUTH_SUCCESS);
        if (domains.isEmpty()) {
            return ResponseEntity.ok(loginUsers);
        }
        for (UserAuthEvent domain : domains.getContent()) {
            Optional<User> user = userRepository.findById(domain.getUsername());
            if (user.isPresent()) {
                AuthUser loginUser = new AuthUser();
                BeanUtils.copyProperties(user.get(), loginUser);
                loginUser.setSignInAt(domain.getCreatedAt());
                loginUsers.add(loginUser);
            }
        }
        return ResponseEntity.ok().headers(generatePageHeaders(domains)).body(loginUsers);
    }

    @Operation(summary = "get user login count in last seven days")
    @GetMapping("/api/user-auth-events/user-login-count")
    public ResponseEntity<List<UserLoginCount>> getUserLoginCount() {
        User user = userService.getCurrentUser();
        List<UserLoginCount> userLoginCounts = new ArrayList<>();
        Instant now = Instant.now();
        ZonedDateTime zonedDateTime = now.atZone(ZoneId.of(user.getTimeZoneId()));
        ZonedDateTime startOfDay = zonedDateTime.toLocalDate().atStartOfDay(zonedDateTime.getZone());
        for (int i = 6; i >= 0; i--) {
            Instant yesterday = startOfDay.toInstant().minus(i, ChronoUnit.DAYS);
            Long loginCount = userAuthEventRepository.countByEventAndCreatedAtBetween(AUTH_SUCCESS, yesterday, yesterday.plus(1, ChronoUnit.DAYS));
            UserLoginCount userLoginCount = new UserLoginCount();
            userLoginCount.setCalculatedAt(DateTimeFormatter.ofPattern("yyyy-MM-dd").format(yesterday.atZone(ZoneId.of(user.getTimeZoneId()))));
            userLoginCount.setLoginCount(loginCount);
            userLoginCounts.add(userLoginCount);
        }
        return ResponseEntity.ok().body(userLoginCounts);
    }

    @Operation(summary = "delete user auth event by id", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/user-auth-events/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        userAuthEventRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
