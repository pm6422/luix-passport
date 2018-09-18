package org.infinity.passport.service.impl;

import com.codahale.metrics.annotation.Timed;
import org.apache.commons.collections.CollectionUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.entity.MenuTree;
import org.infinity.passport.entity.MenuTreeNode;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.stream.Collectors;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    @Autowired
    private AdminMenuRepository       adminMenuRepository;
    @Autowired
    private AuthorityAdminMenuService authorityAdminMenuService;

    @Override
    public List<MenuTreeNode> getAllAuthorityMenus(String appName, String enabledAuthority) {
        Set<String> adminMenuIds = authorityAdminMenuService.findAdminMenuIdSetByAuthorityNameIn(Arrays.asList(enabledAuthority));
        List<AdminMenuDTO> allAdminMenus = adminMenuRepository.findByAppName(appName).stream().map(menu -> {
            AdminMenuDTO dto = menu.asDTO();
            if (adminMenuIds.contains(menu.getId())) {
                dto.setChecked(true);
            }
            return dto;
        }).collect(Collectors.toList());
        return this.groupAdminMenuDTO(allAdminMenus);
    }

    @Override
    @Timed
    public List<MenuTreeNode> getAuthorityMenus(String appName, List<String> enabledAuthorities) {
        if (CollectionUtils.isEmpty(enabledAuthorities)) {
            return Collections.emptyList();
        }

        Set<String> adminMenuIds = authorityAdminMenuService.findAdminMenuIdSetByAuthorityNameIn(enabledAuthorities);
        if (CollectionUtils.isNotEmpty(adminMenuIds)) {
            List<AdminMenu> adminMenus = adminMenuRepository.findByAppNameAndIdIn(appName, adminMenuIds);
            return this.groupAdminMenu(adminMenus);
        }
        return Collections.emptyList();
    }

    private List<MenuTreeNode> groupAdminMenuDTO(List<AdminMenuDTO> menus) {
        MenuTree tree = new MenuTree();
        menus.forEach(menu -> tree.insert(menu.asNode()));
        tree.sort();
        return tree.getChildren();
    }

    private List<MenuTreeNode> groupAdminMenu(List<AdminMenu> menus) {
        MenuTree tree = new MenuTree();
        menus.forEach(menu -> tree.insert(menu.asNode()));
        tree.sort();
        return tree.getChildren();
    }

    @Override
    public List<AdminMenu> getAuthorityLinks(String appName, List<String> enabledAuthorities) {
        List<AdminMenu> results = new ArrayList<>();
        if (CollectionUtils.isEmpty(enabledAuthorities)) {
            return results;
        }

        Set<String> adminMenuIds = authorityAdminMenuService.findAdminMenuIdSetByAuthorityNameIn(enabledAuthorities);
        if (CollectionUtils.isNotEmpty(adminMenuIds)) {
            return adminMenuRepository.findByAppNameAndIdInAndLevelGreaterThan(appName, new ArrayList<>(adminMenuIds), 1);
        }
        return results;
    }

    @Override
    public Map<String, Integer> getParentIdSeqMap() {
        return adminMenuRepository.findByLevel(1).stream().collect(Collectors.toMap(AdminMenu::getId, AdminMenu::getSequence));
    }

    @Override
    public void raiseSeq(String id) {
        //        AdminMenu current = adminMenuRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        //        List<AdminMenu> existings = adminMenuRepository.findByAppNameAndLevelOrderBySequenceAsc(current.getAppName(),
        //                current.getLevel());
        //        if (CollectionUtils.isNotEmpty(existings) && existings.size() == 1) {
        //            return;
        //        }
        //        LinkedList<AdminMenu> linkedList = new LinkedList<>(existings);
        //        int currentIndex = linkedList.indexOf(current);
        //
        //        if (!linkedList.getLast().equals(current)) {
        //            // Raise sequence
        //            linkedList.remove(currentIndex);
        //            linkedList.add(currentIndex - 1, current);
        //        }
        //
        //        // Re-set the ask precedence
        //        for (int i = 0; i < linkedList.size(); i++) {
        //            linkedList.get(i).setSequence(linkedList.get(i).getLevel() + i);
        //        }

        //        this.updateAll(linkedList);

    }

    @Override
    public void lowerSeq(String id) {
        // TODO Auto-generated method stub

    }
}