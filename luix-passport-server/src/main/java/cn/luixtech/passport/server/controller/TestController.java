package cn.luixtech.passport.server.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.BCRYPT_PASSWORD_ENCODER;
import static cn.luixtech.passport.server.config.AuthorizationServerConfiguration.DEFAULT_PASSWORD_ENCODER_PREFIX;

@RestController
public class TestController {
    @Operation(summary = "get encoded password")
    @GetMapping("/api/test/encode-password/{rawPassword}")
    public String encodePassword(@Parameter(description = "Raw password", required = true) @PathVariable String rawPassword) {
        return DEFAULT_PASSWORD_ENCODER_PREFIX + BCRYPT_PASSWORD_ENCODER.encode(rawPassword);
    }
}
