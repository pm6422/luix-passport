package org.infinity.passport.service.impl;

import org.infinity.passport.domain.AuthorityAdminMenu;
import org.infinity.passport.repository.AuthorityAdminMenuRepository;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AuthorityAdminMenuServiceImpl implements AuthorityAdminMenuService {

    private final AuthorityAdminMenuRepository authorityAdminMenuRepository;

    public AuthorityAdminMenuServiceImpl(AuthorityAdminMenuRepository authorityAdminMenuRepository) {
        this.authorityAdminMenuRepository = authorityAdminMenuRepository;
    }

    @Override
    public Set<String> findAdminMenuIds(List<String> authorityNames) {
        return authorityAdminMenuRepository.findByAuthorityNameIn(authorityNames).stream()
                .map(AuthorityAdminMenu::getAdminMenuId).collect(Collectors.toSet());
    }
}