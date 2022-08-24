package com.luixtech.passport.service;

import com.luixtech.passport.domain.App;

public interface AppService {

    App insert(App domain);

    void update(App domain);

}