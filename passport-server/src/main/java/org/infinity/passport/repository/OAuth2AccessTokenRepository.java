package org.infinity.passport.repository;

import org.infinity.passport.domain.MongoOAuth2AccessToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

import java.util.Date;
import java.util.List;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationAccessToken entity.
 */
@Repository
public interface OAuth2AccessTokenRepository extends MongoRepository<MongoOAuth2AccessToken, String> {

    MongoOAuth2AccessToken findByRefreshToken(String refreshToken);

    MongoOAuth2AccessToken findByAuthenticationId(String authenticationId);

    List<MongoOAuth2AccessToken> findByClientIdAndUserName(String clientId, String userName);

    List<MongoOAuth2AccessToken> findByClientId(String clientId);

    void deleteByExpirationBefore(Date now);

}
