package org.infinity.passport.config.security;

import lombok.extern.slf4j.Slf4j;
import org.infinity.passport.domain.MongoOAuth2ClientDetails;
import org.infinity.passport.repository.OAuth2ClientDetailsRepository;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.provider.*;
import org.springframework.security.oauth2.provider.client.BaseClientDetails;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@Slf4j
public class MongoClientDetailsService implements ClientDetailsService, ClientRegistrationService {

    private final PasswordEncoder               passwordEncoder;
    private final OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository;

    public MongoClientDetailsService(PasswordEncoder passwordEncoder, OAuth2ClientDetailsRepository oAuth2ClientDetailsRepository) {
        this.passwordEncoder = passwordEncoder;
        this.oAuth2ClientDetailsRepository = oAuth2ClientDetailsRepository;
    }

    @Override
    public ClientDetails loadClientByClientId(String clientId) throws ClientRegistrationException {
        return oAuth2ClientDetailsRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client found with id: " + clientId));
    }

    @Override
    public void addClientDetails(ClientDetails clientDetails) throws ClientAlreadyExistsException {
        oAuth2ClientDetailsRepository.findById(clientDetails.getClientId()).ifPresent((existingEntity) -> {
            throw new ClientAlreadyExistsException("Client already exists with ID: " + clientDetails.getClientId());
        });
        saveClientDetails(new MongoOAuth2ClientDetails(), clientDetails);
    }

    @Override
    public void updateClientDetails(ClientDetails clientDetails) throws NoSuchClientException {
        MongoOAuth2ClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findById(clientDetails.getClientId())
                .orElseThrow(() -> new NoSuchClientException("No client found with ID: " + clientDetails.getClientId()));
        saveClientDetails(mongoClientDetails, clientDetails);
    }

    @Override
    public void updateClientSecret(String clientId, String secret) throws NoSuchClientException {
        MongoOAuth2ClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client found with id: " + clientId));
        mongoClientDetails.setClientSecret(passwordEncoder.encode(secret));
        oAuth2ClientDetailsRepository.save(mongoClientDetails);
    }

    @Override
    public void removeClientDetails(String clientId) throws NoSuchClientException {
        MongoOAuth2ClientDetails mongoClientDetails = oAuth2ClientDetailsRepository.findById(clientId)
                .orElseThrow(() -> new NoSuchClientException("No client found with id: " + clientId));
        oAuth2ClientDetailsRepository.delete(mongoClientDetails);
    }

    @Override
    public List<ClientDetails> listClientDetails() {
        return new ArrayList<>(oAuth2ClientDetailsRepository.findAll());
    }

    public void saveClientDetails(MongoOAuth2ClientDetails mongoClientDetails, ClientDetails clientDetails) {
        mongoClientDetails.setClientId(clientDetails.getClientId());
        String clientSecret = clientDetails.getClientSecret() != null ? passwordEncoder.encode(clientDetails.getClientSecret()) : null;
        mongoClientDetails.setClientSecret(clientSecret);
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
