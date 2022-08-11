package org.infinity.passport.repository;

import org.infinity.passport.domain.MongoOAuth2RefreshToken;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

/**
 * Spring Data MongoDB repository for the OAuth2AuthenticationRefreshToken entity.
 */
@Repository
public interface OAuth2RefreshTokenRepository extends MongoRepository<MongoOAuth2RefreshToken, String> {

}
