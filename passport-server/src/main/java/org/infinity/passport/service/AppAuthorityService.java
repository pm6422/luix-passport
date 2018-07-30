package org.infinity.passport.service;

import org.infinity.passport.domain.AppAuthority;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface AppAuthorityService {

    Page<AppAuthority> findByAppNameAndAuthorityNameCombinations(Pageable pageable, String appName,
            String authorityName);

}