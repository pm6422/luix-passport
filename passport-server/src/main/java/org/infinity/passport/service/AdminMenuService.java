package org.infinity.passport.service;

import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.entity.MenuTreeNode;

import java.util.List;

public interface AdminMenuService {

    List<MenuTreeNode> getAllAuthorityMenus(String appName, String enabledAuthority);

    List<MenuTreeNode> getAuthorityMenus(String appName, List<String> enabledAuthorities);

    List<AdminMenu> getAuthorityLinks(String appName, List<String> enabledAuthorities);

    void raiseSeq(String id);

    void lowerSeq(String id);
}