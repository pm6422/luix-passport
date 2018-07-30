package org.infinity.passport.service.impl;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.StringUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.dto.AdminManagedMenuDTO;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.domain.Sort.Direction;
import org.springframework.stereotype.Service;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    @Autowired
    private AdminMenuRepository       adminMenuRepository;

    @Autowired
    private AuthorityAdminMenuService authorityAdminMenuService;

    @Override
    public AdminMenu insert(String appName, String adminMenuName, String adminMenuChineseText, String link,
            Integer sequence, String parentMenuId) {
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.setAppName(appName);
        adminMenu.setAdminMenuName(adminMenuName);
        adminMenu.setAdminMenuChineseText(adminMenuChineseText);

        Integer level = 1;
        if (StringUtils.isNotEmpty(parentMenuId)) {
            level = adminMenuRepository.findById(parentMenuId).get().getLevel() + 1;
        }
        adminMenu.setLevel(level);
        adminMenu.setLink(link);
        adminMenu.setSequence(sequence);
        adminMenu.setParentMenuId(parentMenuId);

        return adminMenuRepository.save(adminMenu);
    }

    @Override
    public AdminMenu update(String id, String appName, String adminMenuName, String adminMenuChineseText, Integer level,
            String link, Integer sequence, String parentMenuId) {
        AdminMenu entity = adminMenuRepository.findById(id).get();
        entity.setAppName(appName);
        entity.setAdminMenuName(adminMenuName);
        entity.setAdminMenuChineseText(adminMenuChineseText);
        entity.setLevel(level);
        entity.setLink(link);
        entity.setSequence(sequence);
        entity.setParentMenuId(parentMenuId);

        return adminMenuRepository.save(entity);
    }

    @Override
    public List<AdminManagedMenuDTO> classifyAdminMenu(List<AdminMenuDTO> adminMenuList) {
        List<AdminManagedMenuDTO> result = new ArrayList<AdminManagedMenuDTO>();
        Map<String, AdminManagedMenuDTO> idMenuMap = new HashMap<String, AdminManagedMenuDTO>();
        int level = 1;
        AdminManagedMenuDTO parent = null;
        while (idMenuMap.size() < adminMenuList.size()) {
            for (AdminMenuDTO adminMenu : adminMenuList) {
                if (level != adminMenu.getLevel().intValue()) {
                    continue;
                }
                parent = idMenuMap.get(adminMenu.getParentMenuId());
                if (parent == null) {
                    // 如果parent为空，则表示是顶级元素
                    parent = new AdminManagedMenuDTO();
                    BeanUtils.copyProperties(adminMenu, parent);
                    result.add(parent);
                    idMenuMap.put(parent.getId(), parent);
                } else {
                    List<AdminManagedMenuDTO> subItems = parent.getSubItems();
                    if (subItems == null) {
                        subItems = new ArrayList<AdminManagedMenuDTO>();
                        parent.setSubItems(subItems);
                    }
                    AdminManagedMenuDTO parentItem = new AdminManagedMenuDTO();
                    BeanUtils.copyProperties(adminMenu, parentItem);
                    subItems.add(parentItem);
                    idMenuMap.put(adminMenu.getId(), parentItem);
                }
            }
            level++;
        }
        // Sequence升序
        Comparator<AdminManagedMenuDTO> seqAscComparator = (c1, c2) -> c1.getSequence().compareTo(c2.getSequence());
        this.sort(result, seqAscComparator);
        return result;
    }

    @Override
    public List<AdminManagedMenuDTO> getAuthorityMenus(String appName, List<String> enabledAuthorities) {
        List<AdminManagedMenuDTO> results = new ArrayList<AdminManagedMenuDTO>();
        if (CollectionUtils.isEmpty(enabledAuthorities)) {
            return results;
        }

        Set<String> authorityAdminMenuIds = authorityAdminMenuService
                .findAdminMenuIdSetByAuthorityNameIn(enabledAuthorities);

        if (CollectionUtils.isNotEmpty(authorityAdminMenuIds)) {
            Sort sort = new Sort(Direction.ASC, AdminMenu.FIELD_LEVEL);
            List<AdminMenuDTO> amdinMenusDTO = adminMenuRepository
                    .findByAppNameAndIdIn(sort, appName, new ArrayList<>(authorityAdminMenuIds)).stream()
                    .map(adminMenu -> adminMenu.asDTO()).collect(Collectors.toList());
            results = this.classifyAdminMenu(amdinMenusDTO);
        }
        return results;
    }

    @Override
    public List<AdminMenuDTO> getAuthorityLinks(String appName, List<String> enabledAuthorities) {
        List<AdminMenuDTO> results = new ArrayList<AdminMenuDTO>();
        if (CollectionUtils.isEmpty(enabledAuthorities)) {
            return results;
        }

        Set<String> authorityAdminMenuIds = authorityAdminMenuService
                .findAdminMenuIdSetByAuthorityNameIn(enabledAuthorities);

        if (CollectionUtils.isNotEmpty(authorityAdminMenuIds)) {
            return adminMenuRepository
                    .findByAppNameAndIdInAndLevelGreaterThan(appName, new ArrayList<>(authorityAdminMenuIds), 1)
                    .stream().map(adminMenu -> adminMenu.asDTO()).collect(Collectors.toList());
        }
        return results;
    }

    /**
     * 排序方法
     * 
     * @param list
     */
    private void sort(List<AdminManagedMenuDTO> list, Comparator<AdminManagedMenuDTO> adminMenuDTOComparator) {
        if (CollectionUtils.isEmpty(list)) {
            return;
        }
        for (AdminManagedMenuDTO managedAdminMenuDTO : list) {
            sort(managedAdminMenuDTO.getSubItems(), adminMenuDTOComparator);
        }
        // 排序
        Collections.sort(list, adminMenuDTOComparator);
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