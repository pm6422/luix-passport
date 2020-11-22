package org.infinity.passport.service.impl;

import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.domain.AppAuthority;
import org.infinity.passport.dto.AdminMenuDTO;
import org.infinity.passport.entity.MenuTree;
import org.infinity.passport.entity.MenuTreeNode;
import org.infinity.passport.exception.NoDataException;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
public class AdminMenuServiceImpl implements AdminMenuService {

    private final AdminMenuRepository       adminMenuRepository;
    private final AuthorityAdminMenuService authorityAdminMenuService;

    public AdminMenuServiceImpl(AdminMenuRepository adminMenuRepository, AuthorityAdminMenuService authorityAdminMenuService) {
        this.adminMenuRepository = adminMenuRepository;
        this.authorityAdminMenuService = authorityAdminMenuService;
    }

    @Override
    public Page<AdminMenu> find(Pageable pageable, String appName) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Example<AdminMenu> queryExample = Example.of(new AdminMenu(appName), matcher);
        return adminMenuRepository.findAll(queryExample, pageable);
    }

    @Override
    public List<MenuTreeNode> getAllAuthorityMenus(String appName, String enabledAuthority) {
        Set<String> adminMenuIds = authorityAdminMenuService.findAdminMenuIdSetByAuthorityNameIn(Collections.singletonList(enabledAuthority));
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
        MenuTree tree = new MenuTree(menus.stream().map(AdminMenuDTO::asNode).collect(Collectors.toList()));
        return tree.getChildren();
    }

    private List<MenuTreeNode> groupAdminMenu(List<AdminMenu> menus) {
        MenuTree tree = new MenuTree(menus.stream().map(AdminMenu::asNode).collect(Collectors.toList()));
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
    public void raiseSeq(String id) {
        this.adjustSeq(id, -1, this::isNotHead);
    }

    private boolean isNotHead(LinkedList<AdminMenu> linkedList, AdminMenu current) {
        return !linkedList.getFirst().equals(current);
    }

    @Override
    public void lowerSeq(String id) {
        this.adjustSeq(id, 1, this::isNotTail);
    }

    private boolean isNotTail(LinkedList<AdminMenu> linkedList, AdminMenu current) {
        return !linkedList.getLast().equals(current);
    }

    private void adjustSeq(String id, int moveIndex, BiFunction<LinkedList<AdminMenu>, AdminMenu, Boolean> func) {
        AdminMenu current = adminMenuRepository.findById(id).orElseThrow(() -> new NoDataException(id));
        List<AdminMenu> existingList = adminMenuRepository.findByAppNameAndLevelOrderBySequenceAsc(current.getAppName(), current.getLevel());
        if (CollectionUtils.isNotEmpty(existingList) && existingList.size() == 1) {
            return;
        }
        LinkedList<AdminMenu> linkedList = new LinkedList<>(existingList);
        int currentIndex = linkedList.indexOf(current);

        if (func.apply(linkedList, current)) {
            linkedList.remove(currentIndex);
            linkedList.add(currentIndex + moveIndex, current);
        }

        // Re-set the sequence
        for (int i = 0; i < linkedList.size(); i++) {
            linkedList.get(i).setSequence(i);
        }
        adminMenuRepository.saveAll(linkedList);
    }
}