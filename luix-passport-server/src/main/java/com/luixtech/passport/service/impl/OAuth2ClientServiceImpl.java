package com.luixtech.passport.service.impl;

import com.google.common.collect.ImmutableMap;
import com.luixtech.passport.domain.oauth2.OAuth2Client;
import com.luixtech.passport.domain.oauth2.OAuth2ClientSettings;
import com.luixtech.passport.domain.oauth2.OAuth2TokenSettings;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.exception.DuplicationException;
import com.luixtech.passport.repository.oauth2.OAuth2ClientRepository;
import com.luixtech.passport.service.OAuth2ClientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.time.Duration;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Optional;

@Service
@AllArgsConstructor
public class OAuth2ClientServiceImpl implements OAuth2ClientService {
    private final OAuth2ClientRepository oAuth2ClientRepository;
    private final PasswordEncoder        passwordEncoder;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void insert(OAuth2Client domain) {
        oAuth2ClientRepository.findById(domain.getClientId()).ifPresent((existingEntity) -> {
            throw new DuplicationException(ImmutableMap.of("clientId", domain.getClientId()));
        });
        domain.setClientSecret(passwordEncoder.encode(domain.getRawClientSecret()));
        domain.setClientIdIssuedAt(Instant.now());
        domain.setClientSecretExpiresAt(domain.getClientIdIssuedAt().plus(domain.getValidityInDays(), ChronoUnit.DAYS));

        domain.getClientAuthenticationMethods().forEach(method -> method.setClientId(domain.getClientId()));
        domain.getAuthorizationGrantTypes().forEach(type -> type.setClientId(domain.getClientId()));
        domain.getRedirectUris().forEach(uri -> uri.setClientId(domain.getClientId()));
        domain.getScopes().forEach(scope -> scope.setClientId(domain.getClientId()));
        OAuth2ClientSettings clientSettings = new OAuth2ClientSettings();
        clientSettings.setClientId(domain.getClientId());
        clientSettings.setRequireProofKey(false);
        clientSettings.setRequireAuthorizationConsent(true);
        domain.setClientSettings(clientSettings);

        OAuth2TokenSettings tokenSettings = new OAuth2TokenSettings();
        tokenSettings.setClientId(domain.getClientId());
        tokenSettings.setAccessTokenTimeToLive(Duration.of(7, ChronoUnit.DAYS));
        tokenSettings.setRefreshTokenTimeToLive(Duration.of(30, ChronoUnit.DAYS));
        tokenSettings.setTokenFormat("self-contained");
        tokenSettings.setReuseRefreshTokens(true);
        tokenSettings.setIdTokenSignatureAlgorithm("RS256");
        domain.setTokenSettings(tokenSettings);

        oAuth2ClientRepository.save(domain);
    }

    @Override
    public Page<OAuth2Client> find(Pageable pageable, String clientId) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        OAuth2Client client = new OAuth2Client();
        client.setClientId(clientId);
        Example<OAuth2Client> queryExample = Example.of(client, matcher);
        return oAuth2ClientRepository.findAll(queryExample, pageable);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public Optional<OAuth2Client> findById(String id) {
        return oAuth2ClientRepository.findById(id);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void update(OAuth2Client domain) {
        OAuth2Client existingClient = oAuth2ClientRepository.findById(domain.getId()).orElseThrow(() -> new DataNotFoundException(domain.getId()));
        existingClient.setClientIdIssuedAt(Instant.now());
        existingClient.setClientSecretExpiresAt(existingClient.getClientIdIssuedAt().plus(domain.getValidityInDays(), ChronoUnit.DAYS));
        existingClient.setRemarks(domain.getRemarks());
        existingClient.setClientAuthenticationMethods(domain.getClientAuthenticationMethods());
        existingClient.setAuthorizationGrantTypes(domain.getAuthorizationGrantTypes());
        existingClient.setRedirectUris(domain.getRedirectUris());
        existingClient.setScopes(domain.getScopes());

        existingClient.getClientAuthenticationMethods().forEach(method -> method.setClientId(existingClient.getClientId()));
        existingClient.getAuthorizationGrantTypes().forEach(type -> type.setClientId(existingClient.getClientId()));
        existingClient.getRedirectUris().forEach(uri -> uri.setClientId(existingClient.getClientId()));
        existingClient.getScopes().forEach(scope -> scope.setClientId(existingClient.getClientId()));

        oAuth2ClientRepository.save(existingClient);
    }
}