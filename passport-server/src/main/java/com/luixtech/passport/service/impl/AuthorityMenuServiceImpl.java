package com.luixtech.passport.service.impl;

import com.luixtech.passport.domain.AuthorityMenu;
import com.luixtech.passport.repository.AuthorityMenuRepository;
import com.luixtech.passport.service.AuthorityMenuService;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

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

    @Override
    @Transactional(propagation = Propagation.REQUIRED, noRollbackFor = Exception.class)
    public void deleteByAuthorityName(String authorityName) {
        authorityMenuRepository.deleteByAuthorityName(authorityName);
    }
}