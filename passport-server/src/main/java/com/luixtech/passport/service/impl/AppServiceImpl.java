package com.luixtech.passport.service.impl;

import com.luixtech.passport.domain.App;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.AppAuthorityRepository;
import com.luixtech.passport.repository.AppRepository;
import com.luixtech.passport.service.AppService;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AppServiceImpl implements AppService {
    private final AppRepository          appRepository;
    private final AppAuthorityRepository appAuthorityRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public App insert(App domain) {
        appRepository.save(domain);
        log.debug("Created information for app: {}", domain);
        return domain;
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void update(App domain) {
        appRepository.findById(domain.getId()).map(app -> {
            app.setEnabled(domain.getEnabled());
            app.setAuthorities(domain.getAuthorities());
            appRepository.save(app);
            log.debug("Updated app: {}", app);
            return app;
        }).orElseThrow(() -> new DataNotFoundException(domain.getId()));
    }
}