package org.infinity.passport.service.impl;

import org.infinity.passport.domain.Authority;
import org.infinity.passport.repository.AuthorityRepository;
import org.infinity.passport.service.AuthorityService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class AuthorityServiceImpl implements AuthorityService {

    private final AuthorityRepository authorityRepository;

    public AuthorityServiceImpl(AuthorityRepository authorityRepository) {
        this.authorityRepository = authorityRepository;
    }

    @Override
    public List<String> findAllAuthorityNames(Boolean enabled) {
        return authorityRepository.findByEnabled(enabled).stream().map(Authority::getName)
                .collect(Collectors.toList());
    }

    @Override
    public List<String> findAllAuthorityNames() {
        return authorityRepository.findAll().stream().map(Authority::getName)
                .collect(Collectors.toList());
    }
}