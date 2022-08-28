package com.luixtech.passport.controller;

import com.luixtech.passport.component.HttpHeaderCreator;
import com.luixtech.passport.domain.Authority;
import com.luixtech.passport.domain.oauth2.OAuth2Client;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.oauth2.OAuth2ClientRepository;
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
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.Valid;
import java.util.List;

import static com.luixtech.passport.config.api.SpringDocConfiguration.AUTH;
import static com.luixtech.passport.utils.HttpHeaderUtils.generatePageHeaders;

@Slf4j
@RestController
@SecurityRequirement(name = AUTH)
@AllArgsConstructor
public class OAuth2ClientController {
    private final HttpHeaderCreator      httpHeaderCreator;
    private final OAuth2ClientRepository oAuth2ClientRepository;
    private final OAuth2ClientService    oAuth2ClientService;

    @Operation(summary = "create a new OAuth2 client")
    @PostMapping("/api/oauth2-clients")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> create(@Parameter(description = "OAuth2 client", required = true) @Valid @RequestBody OAuth2Client domain) {
        log.debug("REST create oauth client: {}", domain);
        oAuth2ClientService.insert(domain);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", domain.getClientId()))
                .build();
    }

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
        Page<OAuth2Client> domains = oAuth2ClientService.find(pageable, clientId);
        HttpHeaders headers = generatePageHeaders(domains);
        return ResponseEntity.ok().headers(headers).body(domains.getContent());
    }

    @Operation(summary = "find oauth2 client by ID")
    @GetMapping("/api/oauth2-clients/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<OAuth2Client> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        OAuth2Client domain = oAuth2ClientService.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update oauth2 client")
    @PutMapping("/api/oauth2-clients")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> update(@Parameter(description = "OAuth2 client", required = true) @Valid @RequestBody OAuth2Client domain) {
        log.debug("REST request to update oauth client detail: {}", domain);
        oAuth2ClientService.update(domain);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1002", domain.getClientId()))
                .build();
    }

    @Operation(summary = "delete oauth2 client by ID", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/oauth2-clients/{id}")
    @PreAuthorize("hasAuthority(\"" + Authority.ADMIN + "\")")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        log.debug("REST request to delete oauth client detail: {}", id);
        OAuth2Client client = oAuth2ClientRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oAuth2ClientRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", client.getClientId())).build();
    }
}
