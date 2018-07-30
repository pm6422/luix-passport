package org.infinity.passport.service.impl;

import java.util.Date;

import org.infinity.passport.repository.OAuth2RefreshTokenRepository;
import org.infinity.passport.service.OAuth2RefreshTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.security.oauth2.common.DefaultExpiringOAuth2RefreshToken;
import org.springframework.stereotype.Service;

@Service
public class OAuth2RefreshTokenServiceImpl implements OAuth2RefreshTokenService {

    @Autowired
    private OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    @Override
    @Scheduled(fixedRate = 60_000L * 60 * 24)
    @Async
    public void removeExpiredData() {
        oAuth2RefreshTokenRepository.findAll().forEach(item -> {
            if (((DefaultExpiringOAuth2RefreshToken) item.getoAuth2RefreshToken()).getExpiration().before(new Date())) {
                // Delete expired token
                oAuth2RefreshTokenRepository.deleteById(item.getId());
            }
        });
    }
}
