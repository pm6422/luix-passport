package cn.luixtech.passport.server.service;

import cn.luixtech.passport.server.domain.Shedlock;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ShedlockService {
    Page<Shedlock> find(Pageable pageable, String id);
}
