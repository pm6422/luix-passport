package org.infinity.passport.service.impl;

import lombok.AllArgsConstructor;
import org.infinity.passport.domain.AuthorityMenu;
import org.infinity.passport.repository.AuthorityMenuRepository;
import org.infinity.passport.service.AuthorityMenuService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AuthorityMenuServiceImpl implements AuthorityMenuService {
    private final AuthorityMenuRepository authorityMenuRepository;

    @Override
    public Set<String> findAdminMenuIds(List<String> authorityNames) {
        return authorityMenuRepository.findByAuthorityNameIn(authorityNames).stream()
                .map(AuthorityMenu::getMenuId).collect(Collectors.toSet());
    }
}