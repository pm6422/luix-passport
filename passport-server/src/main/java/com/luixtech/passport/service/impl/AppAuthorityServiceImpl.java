package com.luixtech.passport.service.impl;

import lombok.AllArgsConstructor;
import com.luixtech.passport.domain.AppAuthority;
import com.luixtech.passport.repository.AppAuthorityRepository;
import com.luixtech.passport.service.AppAuthorityService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AppAuthorityServiceImpl implements AppAuthorityService {
    private final AppAuthorityRepository appAuthorityRepository;

    @Override
    public Page<AppAuthority> find(Pageable pageable, String appName, String authorityName) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<AppAuthority> queryExample = Example.of(new AppAuthority(appName, authorityName), matcher);
        return appAuthorityRepository.findAll(queryExample, pageable);
    }
}