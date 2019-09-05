package org.infinity.passport.config.oauth2;

import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private static final Logger                        LOGGER = LoggerFactory.getLogger(MongoClientDetailsService.class);
    @Autowired
    private              PasswordEncoder               passwordEncoder;
    @Autowired
    private              OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        MongoOAuth2ClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findById(clientId)
                .orElseThrow(() -> {
                    LOGGER.error("No client with requested id: " + clientId);
                    return new NoSuchClientException("No client with requested id: " + clientId);
                });
        return mongoClientDetails;
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        Optional<MongoOAuth2ClientDetails> mongoClientDetails = oAuth2ClientDetailsRepository
                .findById(clientDetails.getClientId());
        if (mongoClientDetails.isPresent()) {
            throw new ClientAlreadyExistsException("Client already exists: " + clientDetails.getClientId());
        }
        saveClientDetails(mongoClientDetails.get(), clientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        if (!oAuth2ClientDetailsRepository.findById(clientDetails.getClientId()).isPresent()) {
            throw new NoSuchClientException("No client found with id = " + clientDetails.getClientId());
        }
        saveClientDetails(new MongoOAuth2ClientDetails(), clientDetails);
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        MongoOAuth2ClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client with requested id: " + clientId));
        mongoClientDetails.setClientSecret(passwordEncoder.encode(secret));
        oAuth2ClientDetailsRepository.save(mongoClientDetails);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        MongoOAuth2ClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client with requested id: " + clientId));
        oAuth2ClientDetailsRepository.delete(mongoClientDetails);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return new ArrayList<>(oAuth2ClientDetailsRepository.findAll());
    }

    public void saveClientDetails(MongoOAuth2ClientDetails mongoClientDetails, ClientDetails clientDetails) {
        mongoClientDetails.setClientId(clientDetails.getClientId());
        mongoClientDetails.setClientSecret(
                clientDetails.getClientSecret() != null ? passwordEncoder.encode(clientDetails.getClientSecret())
                        : null);
        mongoClientDetails.setScope(clientDetails.getScope());
        mongoClientDetails.setResourceIds(clientDetails.getResourceIds());
        mongoClientDetails.setAuthorizedGrantTypes(clientDetails.getAuthorizedGrantTypes());
        mongoClientDetails.setRegisteredRedirectUri(clientDetails.getRegisteredRedirectUri());
        mongoClientDetails.setAuthorities(clientDetails.getAuthorities());
        mongoClientDetails.setAccessTokenValiditySeconds(clientDetails.getAccessTokenValiditySeconds());
        mongoClientDetails.setRefreshTokenValiditySeconds(clientDetails.getRefreshTokenValiditySeconds());
        mongoClientDetails.setAdditionalInformation(clientDetails.getAdditionalInformation());
        if (clientDetails instanceof BaseClientDetails) {
            mongoClientDetails.setAutoApproveScopes(((BaseClientDetails) clientDetails).getAutoApproveScopes());
        }
        oAuth2ClientDetailsRepository.save(mongoClientDetails);
    }
}
