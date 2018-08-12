package org.infinity.passport.service;

import org.infinity.passport.collection.tree.GroupedKeysTree;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.dto.AdminMenuDTO;

import java.util.List;

public interface AdminMenuService {

    AdminMenu insert(AdminMenu entity);

    GroupedKeysTree<AdminMenuDTO> getAllAuthorityMenus(String appName, String enabledAuthority);

    GroupedKeysTree<AdminMenu> getAuthorityMenus(String appName, List<String> enabledAuthorities);

    List<AdminMenu> getAuthorityLinks(String appName, List<String> enabledAuthorities);

    void raiseSeq(String id);

    void lowerSeq(String id);
}