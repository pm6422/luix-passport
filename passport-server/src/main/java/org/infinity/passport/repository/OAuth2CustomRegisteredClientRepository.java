package org.infinity.passport.repository;

import org.infinity.passport.domain.OAuth2Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;

public interface OAuth2CustomRegisteredClientRepository extends RegisteredClientRepository {

    void save(OAuth2Client client);

    void update(OAuth2Client client);

    Page<OAuth2Client> find(Pageable pageable);

    OAuth2Client findClientById(String id);

    void deleteById(String id);
}