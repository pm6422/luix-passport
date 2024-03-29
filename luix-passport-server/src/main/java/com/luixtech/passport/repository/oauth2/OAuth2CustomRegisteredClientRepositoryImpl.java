package com.luixtech.passport.repository.oauth2;

import com.luixtech.passport.domain.oauth2.OAuth2Client;
import com.luixtech.passport.exception.DataNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.BeanUtils;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClient;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.Assert;

import java.time.Instant;

@AllArgsConstructor
public class OAuth2CustomRegisteredClientRepositoryImpl implements OAuth2CustomRegisteredClientRepository {
    private OAuth2ClientRepository oauth2ClientRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public RegisteredClient findById(String id) {
        Assert.hasText(id, "id cannot be empty");
        return oauth2ClientRepository.findById(id)
                .map(OAuth2Client::toRegisteredClient)
                .orElse(null);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, readOnly = true, noRollbackFor = Exception.class)
    public RegisteredClient findByClientId(String clientId) {
        Assert.hasText(clientId, "clientId cannot be empty");
        return oauth2ClientRepository.findByClientId(clientId)
                .map(OAuth2Client::toRegisteredClient)
                .orElse(null);
    }

    @Override
    public Page<OAuth2Client> find(Pageable pageable) {
        return oauth2ClientRepository.findAll(pageable);
    }

    @Override
    public void save(OAuth2Client oauth2Client) {
        oauth2Client.setClientIdIssuedAt(Instant.now());
        oauth2ClientRepository.save(oauth2Client);
    }

    @Override
    public void save(RegisteredClient registeredClient) {
        Assert.notNull(registeredClient, "registeredClient cannot be null");
        oauth2ClientRepository.save(OAuth2Client.fromRegisteredClient(registeredClient));
    }

    @Override
    public void update(OAuth2Client source) {
        String id = source.getId();
        OAuth2Client existingClient = oauth2ClientRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        // Ignore client secret update
        source.setClientSecret(null);
        BeanUtils.copyProperties(source, existingClient);
        oauth2ClientRepository.save(existingClient);
    }
}
