package org.infinity.passport.service.impl;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.collection.tree.GroupedKeysTree;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    @Autowired
    private AdminMenuRepository       adminMenuRepository;
    @Autowired
    private AuthorityAdminMenuService authorityAdminMenuService;

    @Override
    public AdminMenu insert(AdminMenu entity) {
        Integer level = 1;
        if (StringUtils.isNotEmpty(entity.getParentMenuId())) {
            level = adminMenuRepository.findById(entity.getParentMenuId()).get().getLevel() + 1;
        }

        return adminMenuRepository.save(entity);
    }

    private GroupedKeysTree<AdminMenu> groupAdminMenu(List<AdminMenu> menus) {
        GroupedKeysTree<AdminMenu> tree = new GroupedKeysTree();
        menus.forEach((menu) -> tree.insert(menu, menu.getParentMenuId(), menu.getId()));
        return tree;
    }

    private GroupedKeysTree<AdminMenuDTO> groupAdminMenuDTO(List<AdminMenuDTO> menus) {
        GroupedKeysTree<AdminMenuDTO> tree = new GroupedKeysTree();
        menus.forEach((menu) -> tree.insert(menu, menu.getParentMenuId(), menu.getId()));
        return tree;
    }

    @Override
    public GroupedKeysTree<AdminMenuDTO> getAllAuthorityMenus(String appName, String enabledAuthority) {
        Set<String> adminMenuIds = authorityAdminMenuService.findAdminMenuIdSetByAuthorityNameIn(Arrays.asList(enabledAuthority));
        Sort sort = new Sort(Direction.ASC, AdminMenu.FIELD_SEQUENCE);
        List<AdminMenuDTO> allAdminMenus = adminMenuRepository.findByAppName(sort, appName).stream().map(menu -> {
            AdminMenuDTO DTO = menu.asDTO();
            if (adminMenuIds.contains(menu.getId())) {
                DTO.setChecked(true);
            }
            return DTO;
        }).collect(Collectors.toList());
        return this.groupAdminMenuDTO(allAdminMenus);
    }

    @Override
    public GroupedKeysTree<AdminMenu> getAuthorityMenus(String appName, List<String> enabledAuthorities) {
        if (CollectionUtils.isEmpty(enabledAuthorities)) {
            return null;
        }

        Set<String> adminMenuIds = authorityAdminMenuService.findAdminMenuIdSetByAuthorityNameIn(enabledAuthorities);
        if (CollectionUtils.isNotEmpty(adminMenuIds)) {
            Sort sort = new Sort(Direction.ASC, AdminMenu.FIELD_SEQUENCE);
            List<AdminMenu> adminMenus = adminMenuRepository.findByAppNameAndIdIn(sort, appName, adminMenuIds);
            return this.groupAdminMenu(adminMenus);
        }
        return null;
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