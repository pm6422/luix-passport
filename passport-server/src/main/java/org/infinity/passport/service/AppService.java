package org.infinity.passport.service;

import org.infinity.passport.domain.App;

import java.util.Set;

public interface AppService {

    App insert(String name, Boolean enabled, Set<String> authorityNames);

    void update(String name, Boolean enabled, Set<String> authorityNames);

}