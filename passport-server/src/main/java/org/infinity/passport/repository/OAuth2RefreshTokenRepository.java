package org.infinity.passport.repository;

import org.infinity.passport.domain.MongoOAuth2RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationRefreshToken entity.
 */
public interface OAuth2RefreshTokenRepository extends MongoRepository<MongoOAuth2RefreshToken, String> {

}
