package org.infinity.passport.service.impl;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

import org.infinity.passport.domain.AuthorityAdminMenu;
import org.infinity.passport.repository.AuthorityAdminMenuRepository;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class AuthorityAdminMenuServiceImpl implements AuthorityAdminMenuService {

    @Autowired
    private AuthorityAdminMenuRepository authorityAdminMenuRepository;

    @Override
    public Set<String> findAdminMenuIdSetByAuthorityNameIn(List<String> authorityNames) {
        return authorityAdminMenuRepository.findByAuthorityNameIn(authorityNames).stream()
                .map(AuthorityAdminMenu::getAdminMenuId).collect(Collectors.toSet());
    }

}