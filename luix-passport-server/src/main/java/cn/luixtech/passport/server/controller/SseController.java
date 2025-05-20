package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.utils.AuthUtils;
import com.luixtech.springbootframework.utils.SseEmitterUtils;
import io.swagger.v3.oas.annotations.Operation;
import jakarta.servlet.http.HttpServletRequest;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class SseController {

    @Operation(summary = "return a SseEmitter HTTP long connection for current user")
    @GetMapping(path = "/api/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        // Return a SseEmitter HTTP long connection
        return Optional.ofNullable(SseEmitterUtils.connect(AuthUtils.getCurrentUserId()))
                .orElseThrow(() -> new IllegalStateException("Can NOT create SSE connection"));
    }

    @Operation(summary = "disconnect SseEmitter HTTP long connection for current user")
    @GetMapping("/api/sse/disconnect")
    public void disconnect(HttpServletRequest request) {
        request.startAsync();
        SseEmitterUtils.removeUser(AuthUtils.getCurrentUserId());
    }
}