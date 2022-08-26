package com.luixtech.passport.service;

import com.luixtech.passport.domain.oauth2.OAuth2Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface OAuth2ClientService {
    Page<OAuth2Client> find(Pageable pageable, String clientId);
}
