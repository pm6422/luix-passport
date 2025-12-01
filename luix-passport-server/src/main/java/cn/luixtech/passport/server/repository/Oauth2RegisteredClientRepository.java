package cn.luixtech.passport.server.repository;

import cn.luixtech.passport.server.domain.Oauth2RegisteredClient;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface Oauth2RegisteredClientRepository extends JpaRepository<Oauth2RegisteredClient, String> {

    List<Oauth2RegisteredClient> findByClientId(String clientId);

    Long countByEnabledIsTrue();
}
