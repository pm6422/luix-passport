package org.infinity.passport.service;

import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.dto.AdminManagedMenuDTO;
import org.infinity.passport.dto.AdminMenuDTO;

import java.util.List;

public interface AdminMenuService {

    AdminMenu insert(AdminMenu entity);

    List<AdminManagedMenuDTO> classifyAdminMenu(List<AdminMenuDTO> data);

    List<AdminManagedMenuDTO> getAuthorityMenus(String appName, List<String> enabledAuthorities);

    List<AdminMenuDTO> getAuthorityLinks(String appName, List<String> enabledAuthorities);

    void raiseSeq(String id);

    void lowerSeq(String id);
}