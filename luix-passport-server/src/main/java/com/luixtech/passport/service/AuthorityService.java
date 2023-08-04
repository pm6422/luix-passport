package com.luixtech.passport.service;

import com.luixtech.passport.domain.Authority;

import java.util.List;

public interface AuthorityService {

    List<String> findAllAuthorityNames(Boolean enabled);

    List<Authority> find(Boolean enabled);

}