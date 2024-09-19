package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.UserAuthEvent;
import cn.luixtech.passport.server.repository.UserAuthEventRepository;
import cn.luixtech.passport.server.service.UserAuthEventService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class UserAuthEventServiceImpl implements UserAuthEventService {
    private final UserAuthEventRepository userAuthEventRepository;

    @Override
    public Page<UserAuthEvent> find(Pageable pageable, String userId, String event) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        UserAuthEvent criteria = new UserAuthEvent();
        criteria.setUserId(userId);
        criteria.setEvent(event);
        Example<UserAuthEvent> queryExample = Example.of(criteria, matcher);
        return userAuthEventRepository.findAll(queryExample, pageable);
    }
}
