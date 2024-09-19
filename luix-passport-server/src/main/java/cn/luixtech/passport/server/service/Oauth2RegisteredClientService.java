package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.pojo.Oauth2Client;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface Oauth2RegisteredClientService {
    void insert(Oauth2Client pojo);

    Oauth2Client findById(String id);

    void update(Oauth2Client domain);

    Page<Oauth2Client> find(Pageable pageable, String clientId);
}
