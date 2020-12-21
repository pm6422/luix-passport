package org.infinity.passport.service;

import org.infinity.passport.domain.App;

public interface AppService {

    App insert(App domain);

    void update(App domain);

}