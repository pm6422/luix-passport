package com.luixtech.passport.controller;

import com.luixtech.passport.component.HttpHeaderCreator;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.oauth2.OAuth2Client;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.service.OAuth2ClientService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.api.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.util.Pair;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

import static com.luixtech.passport.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

@Slf4j
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
public class OAuth2ClientController {
    private final HttpHeaderCreator   httpHeaderCreator;
    private final PasswordEncoder     passwordEncoder;
    private final OAuth2ClientService oAuth2ClientService;

    @Operation(summary = "get internal client")
    @GetMapping("/open-api/oauth2-client/internal-client")
    public ResponseEntity<Pair<String, String>> getInternalClient() {
        return ResponseEntity.ok(Pair.of(OAuth2Client.INTERNAL_CLIENT_ID, OAuth2Client.INTERNAL_RAW_CLIENT_SECRET));
    }

    @Operation(summary = "find oauth2 client list")
    @GetMapping("/api/oauth2-clients")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<List<OAuth2Client>> find(@ParameterObject Pageable pageable,
                                                   @Parameter(description = "Client ID") @RequestParam(value = "clientId", required = false) String clientId) {
        Page<OAuth2Client> clients = oAuth2ClientService.find(pageable, clientId);
        HttpHeaders headers = generatePageHeaders(clients);
        return ResponseEntity.ok().headers(headers).body(clients.getContent());
    }

    @Operation(summary = "find oauth2 client by ID")
    @GetMapping("/api/oauth2-clients/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<OAuth2Client> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        OAuth2Client domain = oAuth2ClientService.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }
}
