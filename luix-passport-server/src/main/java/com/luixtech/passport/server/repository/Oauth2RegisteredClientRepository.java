package com.luixtech.passport.server.repository;

import com.luixtech.passport.server.domain.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;

import java.time.Instant;
import java.util.List;

public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, String> {

    List<Oauth2RegisteredClient> findByClientId(String clientId);

    Long countByEnabledIsTrue();

    long countByCreatedAtAfter(Instant since);
}
