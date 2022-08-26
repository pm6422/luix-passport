package com.luixtech.passport.service.impl;

import com.luixtech.passport.domain.oauth2.OAuth2Client;
import com.luixtech.passport.repository.oauth2.OAuth2ClientRepository;
import com.luixtech.passport.service.OAuth2ClientService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class OAuth2ClientServiceImpl implements OAuth2ClientService {

    private final OAuth2ClientRepository oAuth2ClientRepository;

    @Override
    public Page<OAuth2Client> find(Pageable pageable, String clientId) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        OAuth2Client client = new OAuth2Client();
        client.setClientId(clientId);
        Example<OAuth2Client> queryExample = Example.of(client, matcher);
        return oAuth2ClientRepository.findAll(queryExample, pageable);
    }
}
