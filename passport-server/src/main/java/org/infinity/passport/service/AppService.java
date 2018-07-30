package org.infinity.passport.service;

import java.util.Set;

import org.infinity.passport.domain.App;

public interface AppService {

    App insert(String name, Boolean enabled, Set<String> authorityNames);

    void update(String name, Boolean enabled, Set<String> authorityNames);

}