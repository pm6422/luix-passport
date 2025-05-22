package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.service.SseService;
import cn.luixtech.passport.server.utils.AuthUtils;
import io.swagger.v3.oas.annotations.Operation;
import lombok.AllArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.servlet.mvc.method.annotation.SseEmitter;

import java.util.Optional;

@RestController
@AllArgsConstructor
public class SseController {

    private final SseService sseService;

    @Operation(summary = "return a SseEmitter HTTP long connection for current user")
    @GetMapping(path = "/api/sse/connect", produces = MediaType.TEXT_EVENT_STREAM_VALUE)
    public SseEmitter connect() {
        // Return a SseEmitter HTTP long connection
        return Optional.ofNullable(sseService.add(AuthUtils.getCurrentUserId()))
                .orElseThrow(() -> new IllegalStateException("Failed to create SSE connection"));
    }
}