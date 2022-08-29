package com.luixtech.passport.service.impl;

import com.luixtech.passport.IntegrationTest;
import com.luixtech.passport.domain.App;
import com.luixtech.passport.domain.AppAuthority;
import com.luixtech.passport.repository.AppRepository;
import com.luixtech.passport.service.AppService;
import com.luixtech.uidgenerator.core.id.IdGenerator;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import javax.annotation.Resource;
import java.util.Set;

@IntegrationTest
@Slf4j
public class AppServiceImplIT {

    @Resource
    private AppService    appService;
    @Resource
    private AppRepository appRepository;

    @Test
    @DisplayName("insert app and authorities")
    public void insert() {
        String appName = IdGenerator.generateId();
        App app = new App();
        app.setId(IdGenerator.generateId());
        app.setName(appName);
        app.setAuthorities(Set.of(new AppAuthority(app.getId(), "ROLE_ADMIN")));
        appService.insert(app);
        appRepository.deleteById(app.getId());
    }
}
