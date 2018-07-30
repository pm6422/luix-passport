package org.infinity.passport.service.impl;

import java.util.Date;

import org.infinity.passport.repository.OAuth2AccessTokenRepository;
import org.infinity.passport.service.OAuth2AccessTokenService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Async;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

@Service
public class OAuth2AccessTokenServiceImpl implements OAuth2AccessTokenService {

    @Autowired
    private OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

    @Override
    @Scheduled(fixedRate = 60_000L * 60 * 24)
    @Async
    public void removeExpiredData() {
        oAuth2AccessTokenRepository.deleteByExpirationBefore(new Date());
    }
}
