package com.luixtech.passport.service;

import com.luixtech.passport.domain.Menu;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface MenuService {

    Page<Menu> find(Pageable pageable, String appName);

    List<Menu> getUserAuthorityLinks(String appName);

    List<Menu> getUserAuthorityMenus(String appName);

    List<Menu> getAuthorityMenus(String appName, String authorityName);

    void moveUp(String id);

    void moveDown(String id);
}