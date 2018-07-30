package org.infinity.passport.service.impl;

import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.repository.AppAuthorityRepository;
import org.infinity.passport.service.AppAuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

@Service
public class AppAuthorityServiceImpl implements AppAuthorityService {

    @Autowired
    private AppAuthorityRepository appAuthorityRepository;

    @Override
    public Page<AppAuthority> findByAppNameAndAuthorityNameCombinations(Pageable pageable, String appName,
            String authorityName) {
        if (StringUtils.isEmpty(appName) && StringUtils.isEmpty(authorityName)) {
            return appAuthorityRepository.findAll(pageable);
        } else if (StringUtils.isNotEmpty(appName) && StringUtils.isNotEmpty(authorityName)) {
            return appAuthorityRepository.findByAppNameAndAuthorityName(pageable, appName, authorityName);
        } else if (StringUtils.isNotEmpty(appName) && StringUtils.isEmpty(authorityName)) {
            return appAuthorityRepository.findByAppName(pageable, appName);
        } else {
            return appAuthorityRepository.findByAuthorityName(pageable, authorityName);
        }
    }
}