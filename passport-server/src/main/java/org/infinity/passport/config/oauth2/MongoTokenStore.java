package org.infinity.passport.config.oauth2;

import org.infinity.passport.domain.MongoOAuth2AccessToken;
import org.infinity.passport.domain.MongoOAuth2RefreshToken;
import org.infinity.passport.repository.OAuth2AccessTokenRepository;
import org.infinity.passport.repository.OAuth2RefreshTokenRepository;
import org.springframework.security.oauth2.common.OAuth2AccessToken;
import org.springframework.security.oauth2.common.OAuth2RefreshToken;
import org.springframework.security.oauth2.provider.OAuth2Authentication;
import org.springframework.security.oauth2.provider.token.AuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.DefaultAuthenticationKeyGenerator;
import org.springframework.security.oauth2.provider.token.TokenStore;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

/**
 * A MongoDB implementation of the TokenStore.
 */
public class MongoTokenStore implements TokenStore {

    private final OAuth2AccessTokenRepository oAuth2AccessTokenRepository;

    private final OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository;

    private final AuthenticationKeyGenerator authenticationKeyGenerator = new DefaultAuthenticationKeyGenerator();

    public MongoTokenStore(OAuth2AccessTokenRepository oAuth2AccessTokenRepository, OAuth2RefreshTokenRepository oAuth2RefreshTokenRepository) {
        this.oAuth2AccessTokenRepository = oAuth2AccessTokenRepository;
        this.oAuth2RefreshTokenRepository = oAuth2RefreshTokenRepository;
    }

    @Override
    public OAuth2Authentication readAuthentication(OAuth2AccessToken token) {
        return readAuthentication(token.getValue());
    }

    @Override
    public OAuth2Authentication readAuthentication(String tokenValue) {
        return oAuth2AccessTokenRepository.findById(tokenValue).map(MongoOAuth2AccessToken::getAuthentication).orElse(null);
    }

    @Override
    public void storeAccessToken(OAuth2AccessToken token, OAuth2Authentication authentication) {
        MongoOAuth2AccessToken oAuth2AuthenticationAccessToken = new MongoOAuth2AccessToken(token, authentication,
                authenticationKeyGenerator.extractKey(authentication));
        oAuth2AccessTokenRepository.save(oAuth2AuthenticationAccessToken);
    }

    @Override
    public OAuth2AccessToken readAccessToken(String tokenValue) {
        return oAuth2AccessTokenRepository.findById(tokenValue).map(MongoOAuth2AccessToken::getOAuth2AccessToken).orElse(null);
    }

    @Override
    public void removeAccessToken(OAuth2AccessToken token) {
        oAuth2AccessTokenRepository.findById(token.getValue()).ifPresent(oAuth2AccessTokenRepository::delete);
    }

    @Override
    public void storeRefreshToken(OAuth2RefreshToken refreshToken, OAuth2Authentication authentication) {
        oAuth2RefreshTokenRepository.save(new MongoOAuth2RefreshToken(refreshToken, authentication));
    }

    @Override
    public OAuth2RefreshToken readRefreshToken(String tokenValue) {
        return oAuth2RefreshTokenRepository.findById(tokenValue).map(MongoOAuth2RefreshToken::getOAuth2RefreshToken).orElse(null);
    }

    @Override
    public OAuth2Authentication readAuthenticationForRefreshToken(OAuth2RefreshToken token) {
        return oAuth2RefreshTokenRepository.findById(token.getValue()).map(MongoOAuth2RefreshToken::getAuthentication).orElse(null);
    }

    @Override
    public void removeRefreshToken(OAuth2RefreshToken token) {
        oAuth2RefreshTokenRepository.findById(token.getValue()).ifPresent(oAuth2RefreshTokenRepository::delete);
    }

    @Override
    public void removeAccessTokenUsingRefreshToken(OAuth2RefreshToken refreshToken) {
        MongoOAuth2AccessToken accessToken = oAuth2AccessTokenRepository.findByRefreshToken(refreshToken.getValue());
        if (accessToken != null) {
            oAuth2AccessTokenRepository.delete(accessToken);
        }
    }

    @Override
    public OAuth2AccessToken getAccessToken(OAuth2Authentication authentication) {
        MongoOAuth2AccessToken token = oAuth2AccessTokenRepository.findByAuthenticationId(authenticationKeyGenerator.extractKey(authentication));
        return token == null ? null : token.getOAuth2AccessToken();
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientId(String clientId) {
        List<MongoOAuth2AccessToken> tokens = oAuth2AccessTokenRepository.findByClientId(clientId);
        return extractAccessTokens(tokens);
    }

    @Override
    public Collection<OAuth2AccessToken> findTokensByClientIdAndUserName(String clientId, String userName) {
        List<MongoOAuth2AccessToken> tokens = oAuth2AccessTokenRepository.findByClientIdAndUserName(clientId, userName);
        return extractAccessTokens(tokens);
    }

    private Collection<OAuth2AccessToken> extractAccessTokens(List<MongoOAuth2AccessToken> tokens) {
        List<OAuth2AccessToken> accessTokens = new ArrayList<>();
        for (MongoOAuth2AccessToken token : tokens) {
            accessTokens.add(token.getOAuth2AccessToken());
        }
        return accessTokens;
    }
}
