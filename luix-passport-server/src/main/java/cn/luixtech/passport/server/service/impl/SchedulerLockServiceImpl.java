package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.SchedulerLock;
import cn.luixtech.passport.server.repository.SchedulerLockRepository;
import cn.luixtech.passport.server.service.SchedulerLockService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class SchedulerLockServiceImpl implements SchedulerLockService {
    private final SchedulerLockRepository schedulerLockRepository;

    @Override
    public Page<SchedulerLock> find(Pageable pageable, String id) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("id", match -> match.contains().ignoreCase());

        SchedulerLock criteria = new SchedulerLock();
        criteria.setId(id);

        Example<SchedulerLock> queryExample = Example.of(criteria, matcher);
        return schedulerLockRepository.findAll(queryExample, pageable);
    }
}
