package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.UserAuthEvent;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface UserAuthEventService {

    Page<UserAuthEvent> find(Pageable pageable, String userId, String event);
}
