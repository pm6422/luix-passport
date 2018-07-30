package org.infinity.passport.service.impl;

import java.util.List;
import java.util.stream.Collectors;

import org.infinity.passport.domain.Authority;
import org.infinity.passport.repository.AuthorityRepository;
import org.infinity.passport.service.AuthorityService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    @Autowired
    private AuthorityRepository authorityRepository;

    @Override
    public List<String> findAllAuthorityNames(Boolean enabled) {
        List<String> results = authorityRepository.findByEnabled(enabled).stream().map(Authority::getName)
                .collect(Collectors.toList());
        return results;
    }

    @Override
    public List<String> findAllAuthorityNames() {
        List<String> results = authorityRepository.findAll().stream().map(Authority::getName)
                .collect(Collectors.toList());
        return results;
    }
}