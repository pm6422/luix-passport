package com.luixtech.passport.repository.oauth2;

import com.luixtech.passport.domain.oauth2.OAuth2Client;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

/**
 * Spring Data JPA repository for the {@link OAuth2Client} entity.
 */
@Repository
public interface OAuth2ClientRepository extends JpaRepository<OAuth2Client, String> {

    Optional<OAuth2Client> findByClientId(String clientId);

}
