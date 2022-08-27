package com.luixtech.passport.service.impl;

import com.luixtech.passport.domain.App;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.AppRepository;
import com.luixtech.passport.repository.MenuRepository;
import com.luixtech.passport.service.AppService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

@Service
@AllArgsConstructor
@Slf4j
public class AppServiceImpl implements AppService {
    private final AppRepository  appRepository;
    private final MenuRepository menuRepository;

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void insert(App domain) {
        domain.setId(IdGenerator.generateTraceId());
        domain.getAuthorities().forEach(auth -> auth.setAppId(domain.getId()));
        appRepository.save(domain);
        log.debug("Created information for app: {}", domain);
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void update(App domain) {
        appRepository.findById(domain.getId()).map(app -> {
            app.setEnabled(domain.getEnabled());
            app.setAuthorities(domain.getAuthorities());
            appRepository.save(app);
            log.debug("Updated app: {}", app);

            menuRepository.findByAppName(app.getName()).forEach(menu -> {
                menu.setEnabled(domain.getEnabled());
                menuRepository.save(menu);
                log.debug("Updated menu: {}", menu);
            });
            return app;
        }).orElseThrow(() -> new DataNotFoundException(domain.getId()));
    }

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void deleteById(String id) {
        App app = appRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        appRepository.deleteById(id);
        menuRepository.deleteByAppId(app.getId());
    }
}