package org.infinity.passport.service;

import java.util.List;
import java.util.Set;

public interface AuthorityMenuService {

    Set<String> findAdminMenuIds(List<String> authorityNames);

}