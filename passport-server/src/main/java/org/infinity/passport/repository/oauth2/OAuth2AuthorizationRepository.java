/*
 * Copyright 2022 the original author or authors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.infinity.passport.repository.oauth2;

import org.infinity.passport.domain.oauth2.OAuth2Authorization;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link OAuth2Authorization} entity.
 */
@Repository
@Deprecated
public interface OAuth2AuthorizationRepository extends JpaRepository<OAuth2Authorization, String> {
    Optional<OAuth2Authorization> findByState(String state);

    Optional<OAuth2Authorization> findByAuthorizationCodeValue(String authorizationCode);

    Optional<OAuth2Authorization> findByAccessTokenValue(String accessToken);

    Optional<OAuth2Authorization> findByRefreshTokenValue(String refreshToken);

    Optional<OAuth2Authorization> findByStateOrAuthorizationCodeValueOrAccessTokenValueOrRefreshTokenValue(String token,
                                                                                                           String token2,
                                                                                                           String token3,
                                                                                                           String token4);
}
