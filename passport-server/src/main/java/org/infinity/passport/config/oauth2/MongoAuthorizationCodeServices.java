package org.infinity.passport.config.oauth2;

import org.infinity.passport.domain.MongoOAuth2AuthorizationCode;
import org.infinity.passport.repository.OAuth2AuthorizationCodeRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.code.RandomValueAuthorizationCodeServices;
import org.springframework.stereotype.Service;

@Service
public class MongoAuthorizationCodeServices extends RandomValueAuthorizationCodeServices {

    @Autowired
    private OAuth2AuthorizationCodeRepository oAuth2CodeRepository;

    @Override
    protected void store(String code, OAuth2Authentication authentication) {
        this.oAuth2CodeRepository.save(new MongoOAuth2AuthorizationCode(code, authentication));
    }

    /**
     * Authorization code will be deleted immediately after authentication process.
     */
    @Override
    public OAuth2Authentication remove(String code) {
        MongoOAuth2AuthorizationCode oAuth2AuthenticationCode = oAuth2CodeRepository.findOneByCode(code);
        if (oAuth2AuthenticationCode != null && oAuth2AuthenticationCode.getAuthentication() != null) {
            oAuth2CodeRepository.delete(oAuth2AuthenticationCode);
            return oAuth2AuthenticationCode.getAuthentication();
        }
        return null;
    }
}
