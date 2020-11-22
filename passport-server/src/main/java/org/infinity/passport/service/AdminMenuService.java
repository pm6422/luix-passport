package org.infinity.passport.service;

import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.entity.MenuTreeNode;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface AdminMenuService {

    Page<AdminMenu> find(Pageable pageable, String appName);

    List<MenuTreeNode> getAllAuthorityMenus(String appName, String enabledAuthority);

    List<MenuTreeNode> getAuthorityMenus(String appName, List<String> enabledAuthorities);

    List<AdminMenu> getAuthorityLinks(String appName, List<String> enabledAuthorities);

    void raiseSeq(String id);

    void lowerSeq(String id);
}