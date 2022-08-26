package com.luixtech.passport.service;

import com.luixtech.passport.domain.oauth2.OAuth2Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;

public interface OAuth2ClientService {
    Page<OAuth2Client> find(Pageable pageable, String clientId);

    Optional<OAuth2Client> findById(String id);
}
