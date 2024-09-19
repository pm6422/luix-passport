package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Oauth2RegisteredClient;
import cn.luixtech.passport.server.persistence.Tables;
import cn.luixtech.passport.server.pojo.Oauth2Client;
import cn.luixtech.passport.server.repository.Oauth2RegisteredClientRepository;
import cn.luixtech.passport.server.service.Oauth2RegisteredClientService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import com.luixtech.utilities.exception.DataNotFoundException;
import com.luixtech.utilities.exception.DuplicationException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.jooq.DSLContext;
import org.springframework.data.domain.*;
import org.springframework.security.oauth2.server.authorization.client.RegisteredClientRepository;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

import static cn.luixtech.passport.server.persistence.tables.DataDict.DATA_DICT;
import static cn.luixtech.passport.server.persistence.tables.Oauth2RegisteredClient.OAUTH2_REGISTERED_CLIENT;

@Service
@AllArgsConstructor
public class Oauth2RegisteredClientServiceImpl implements Oauth2RegisteredClientService {
    private final DSLContext                       dslContext;
    private final RegisteredClientRepository       registeredClientRepository;
    private final Oauth2RegisteredClientRepository oauth2RegisteredClientRepository;

    @Override
    public void insert(Oauth2Client pojo) {
        List<Oauth2RegisteredClient> oauth2RegisteredClients = oauth2RegisteredClientRepository.findByClientId(pojo.getClientId());
        if (CollectionUtils.isNotEmpty(oauth2RegisteredClients)) {
            throw new DuplicationException(Map.of("clientId", pojo.getClientId()));
        }
        pojo.setId("O" + IdGenerator.generateShortId());
        registeredClientRepository.save(pojo.toRegisteredClient());
        dslContext.update(Tables.OAUTH2_REGISTERED_CLIENT)
                .set(OAUTH2_REGISTERED_CLIENT.ENABLED, pojo.getEnabled())
                .set(OAUTH2_REGISTERED_CLIENT.CREATED_AT, LocalDateTime.now())
                .set(OAUTH2_REGISTERED_CLIENT.MODIFIED_AT, LocalDateTime.now())
                .where(OAUTH2_REGISTERED_CLIENT.ID.eq(pojo.getId()))
                .execute();
    }

    @Override
    public Oauth2Client findById(String id) {
        return Oauth2Client.fromRegisteredClient(oauth2RegisteredClientRepository.findById(id)
                .orElseThrow(() -> new DataNotFoundException(id)));
    }

    @Override
    public Page<Oauth2Client> find(Pageable pageable, String clientId) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Oauth2RegisteredClient criteria = new Oauth2RegisteredClient();
        criteria.setClientId(clientId);
        Example<Oauth2RegisteredClient> queryExample = Example.of(criteria, matcher);

        Page<Oauth2RegisteredClient> domains = oauth2RegisteredClientRepository.findAll(queryExample, pageable);
        List<Oauth2Client> results = domains.stream().map(Oauth2Client::fromRegisteredClient).collect(Collectors.toList());
        return new PageImpl<>(results, pageable, domains.getNumberOfElements());
    }

    @Override
    public void update(Oauth2Client pojo) {
        Oauth2RegisteredClient existingOne = oauth2RegisteredClientRepository.findById(pojo.getId())
                .orElseThrow(() -> new DataNotFoundException(pojo.getId()));

        Oauth2Client existingClient = Oauth2Client.fromRegisteredClient(existingOne);

        existingClient.setClientSecret(existingOne.getClientSecret());
        existingClient.setClientAuthenticationMethods(pojo.getClientAuthenticationMethods());
        existingClient.setAuthorizationGrantTypes(pojo.getAuthorizationGrantTypes());
        existingClient.setRedirectUris(pojo.getRedirectUris());
        existingClient.setPostLogoutRedirectUris(pojo.getPostLogoutRedirectUris());
        existingClient.setScopes(pojo.getScopes());
        existingClient.setClientSecretExpiresAt(pojo.getClientSecretExpiresAt());
        existingClient.setClientName(pojo.getClientName());

        registeredClientRepository.save(existingClient.toRegisteredClient());

        dslContext.update(Tables.OAUTH2_REGISTERED_CLIENT)
                .set(OAUTH2_REGISTERED_CLIENT.ENABLED, pojo.getEnabled())
                .set(OAUTH2_REGISTERED_CLIENT.MODIFIED_AT, LocalDateTime.now())
                .where(OAUTH2_REGISTERED_CLIENT.ID.eq(pojo.getId()))
                .execute();
    }
}
