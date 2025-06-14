package cn.luixtech.passport.server.service.impl;

import cn.luixtech.passport.server.domain.Shedlock;
import cn.luixtech.passport.server.repository.ShedlockRepository;
import cn.luixtech.passport.server.service.ShedlockService;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class ShedlockServiceImpl implements ShedlockService {
    private final ShedlockRepository shedlockRepository;

    @Override
    public Page<Shedlock> find(Pageable pageable, String id) {
        ExampleMatcher matcher = ExampleMatcher.matching()
                .withIgnoreNullValues()
                .withMatcher("id", match -> match.contains().ignoreCase());

        Shedlock criteria = new Shedlock();
        criteria.setId(id);

        Example<Shedlock> queryExample = Example.of(criteria, matcher);
        return shedlockRepository.findAll(queryExample, pageable);
    }
}
