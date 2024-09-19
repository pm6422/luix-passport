package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.UserAuthEvent;
import cn.luixtech.passport.server.repository.UserAuthEventRepository;
import cn.luixtech.passport.server.service.UserAuthEventService;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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
    private final UserAuthEventService    userAuthEventService;
    private final HttpHeaderCreator       httpHeaderCreator;


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

    @Operation(summary = "delete user auth event by id", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/user-auth-events/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        userAuthEventRepository.deleteById(id);
        return ResponseEntity.ok().headers(httpHeaderCreator.createSuccessHeader("SM1003", id)).build();
    }
}
