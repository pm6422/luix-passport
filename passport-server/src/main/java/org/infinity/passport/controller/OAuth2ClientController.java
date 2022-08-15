package org.infinity.passport.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.component.HttpHeaderCreator;
import org.infinity.passport.domain.OAuth2Client;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.util.Pair;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import static org.infinity.passport.config.api.SpringDocConfiguration.AUTH;

@Slf4j
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
public class OAuth2ClientController {
    private final MongoTemplate     mongoTemplate;
    private final HttpHeaderCreator httpHeaderCreator;
    private final PasswordEncoder   passwordEncoder;

    @Operation(summary = "get internal client")
    @GetMapping("/open-api/oauth2-client/internal-client")
    public ResponseEntity<Pair<String, String>> findInternalClient() {
        return ResponseEntity.ok(Pair.of(OAuth2Client.INTERNAL_CLIENT_ID, OAuth2Client.INTERNAL_RAW_CLIENT_SECRET));
    }
}
