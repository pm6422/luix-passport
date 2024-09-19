package cn.luixtech.passport.server.controller;

import cn.luixtech.passport.server.domain.Oauth2RegisteredClient;
import cn.luixtech.passport.server.pojo.Oauth2Client;
import cn.luixtech.passport.server.repository.Oauth2RegisteredClientRepository;
import cn.luixtech.passport.server.service.Oauth2RegisteredClientService;
import com.luixtech.springbootframework.component.HttpHeaderCreator;
import com.luixtech.utilities.exception.DataNotFoundException;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springdoc.core.annotations.ParameterObject;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.oauth2.core.oidc.OidcScopes;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Optional;
import java.util.Set;

import static cn.luixtech.passport.server.domain.UserRole.ROLE_ADMIN;
import static com.luixtech.springbootframework.utils.HttpHeaderUtils.generatePageHeaders;
import static org.springframework.security.oauth2.core.AuthorizationGrantType.*;
import static org.springframework.security.oauth2.core.ClientAuthenticationMethod.*;
import static org.springframework.security.oauth2.server.authorization.OAuth2TokenType.REFRESH_TOKEN;

@Slf4j
@RestController
@PreAuthorize("hasAuthority(\"" + ROLE_ADMIN + "\")")
@AllArgsConstructor
public class Oauth2ClientController {
    private final HttpHeaderCreator                httpHeaderCreator;
    private final Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;
    private final Oauth2RegisteredClientService    oauth2RegisteredClientService;

    @Operation(summary = "create a new oauth2 registered client")
    @PostMapping("/api/oauth2-clients")
    public ResponseEntity<Void> create(@Parameter(description = "oauth2 registered client", required = true) @Valid @RequestBody Oauth2Client pojo) {
        oauth2RegisteredClientService.insert(pojo);
        return ResponseEntity.status(HttpStatus.CREATED)
                .headers(httpHeaderCreator.createSuccessHeader("SM1001", pojo.getClientId()))
                .build();
    }

    @Operation(summary = "find oauth2 registered client list")
    @GetMapping("/api/oauth2-clients")
    public ResponseEntity<List<Oauth2Client>> find(@ParameterObject Pageable pageable,
                                                   @Parameter(description = "Client ID") @RequestParam(value = "clientId", required = false) String clientId) {
        Page<Oauth2Client> domains = oauth2RegisteredClientService.find(pageable, clientId);
        HttpHeaders headers = generatePageHeaders(domains);
        return ResponseEntity.ok().headers(headers).body(domains.getContent());
    }

    @Operation(summary = "find oauth2 registered client by id")
    @GetMapping("/api/oauth2-clients/{id}")
    public ResponseEntity<Oauth2Client> findById(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Oauth2Client domain = oauth2RegisteredClientService.findById(id);
        return ResponseEntity.ok(domain);
    }

    @Operation(summary = "update oauth2 registered client")
    @PutMapping("/api/oauth2-clients")
    public ResponseEntity<Void> update(@Parameter(description = "oauth2 registered client", required = true) @Valid @RequestBody Oauth2Client pojo) {
        oauth2RegisteredClientService.update(pojo);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1002", pojo.getClientId()))
                .build();
    }

    @Operation(summary = "delete oauth2 registered client by id", description = "the data may be referenced by other data, and some problems may occur after deletion")
    @DeleteMapping("/api/oauth2-clients/{id}")
    public ResponseEntity<Void> delete(@Parameter(description = "ID", required = true) @PathVariable String id) {
        Oauth2RegisteredClient client = oauth2RegisteredClientRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oauth2RegisteredClientRepository.deleteById(id);
        return ResponseEntity.ok()
                .headers(httpHeaderCreator.createSuccessHeader("SM1003", client.getClientId())).build();
    }

    @Operation(summary = "upload photo of the oauth2 registered client")
    @PutMapping("/api/oauth2-clients/photo/upload")
    public void uploadProfilePhoto(@Parameter(description = "id", required = true) @RequestPart String id,
                                   @Parameter(description = "photo", required = true) @RequestPart MultipartFile file) throws IOException {
        Oauth2RegisteredClient oauth2RegisteredClient = oauth2RegisteredClientRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        oauth2RegisteredClient.setPhoto(file.getBytes());
        oauth2RegisteredClientRepository.save(oauth2RegisteredClient);
        log.info("Uploaded oauth2 registered client photo with file name {} and ID {}", file.getOriginalFilename(), id);
    }

    @Operation(summary = "find photo by id")
    @GetMapping("/api/oauth2-clients/photo/{id}")
    public ResponseEntity<byte[]> findPhotoById(@Parameter(description = "id", required = true) @PathVariable String id) {
        Optional<Oauth2RegisteredClient> oauth2RegisteredClient = oauth2RegisteredClientRepository.findById(id);
        return oauth2RegisteredClient.map(photo -> ResponseEntity.ok(photo.getPhoto())).orElse(null);
    }

    @Operation(summary = "get client authentication methods")
    @GetMapping("/api/oauth2-clients/client-authentication-methods")
    public ResponseEntity<Set<String>> getClientAuthenticationMethods() {
        Set<String> sets = new HashSet<>();
        sets.add(CLIENT_SECRET_BASIC.getValue());
        sets.add(CLIENT_SECRET_POST.getValue());
        sets.add(CLIENT_SECRET_JWT.getValue());
        sets.add(PRIVATE_KEY_JWT.getValue());
        sets.add(NONE.getValue());
        return ResponseEntity.ok(sets);
    }

    @Operation(summary = "get authorization grant types")
    @GetMapping("/api/oauth2-clients/authorization-grant-types")
    public ResponseEntity<Set<String>> getAuthorizationGrantTypes() {
        Set<String> sets = new HashSet<>();
        sets.add(AUTHORIZATION_CODE.getValue());
        sets.add(REFRESH_TOKEN.getValue());
        sets.add(CLIENT_CREDENTIALS.getValue());
        sets.add(JWT_BEARER.getValue());
        sets.add(DEVICE_CODE.getValue());
        return ResponseEntity.ok(sets);
    }

    @Operation(summary = "get scopes")
    @GetMapping("/api/oauth2-clients/scopes")
    public ResponseEntity<Set<String>> getScopes() {
        Set<String> sets = new HashSet<>();
        sets.add(OidcScopes.OPENID);
        sets.add(OidcScopes.PROFILE);
        sets.add(OidcScopes.EMAIL);
        sets.add(OidcScopes.PHONE);
        sets.add("external:read");
        sets.add("external:write");
        return ResponseEntity.ok(sets);
    }

    @Operation(summary = "count enabled oauth 2 registered clients")
    @GetMapping("/api/oauth2-clients/count")
    public ResponseEntity<Long> count() {
        return ResponseEntity.ok(oauth2RegisteredClientRepository.count());
    }
}
