package org.infinity.passport.service.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.config.oauth2.SecurityUtils;
import org.infinity.passport.domain.AdminMenu;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.AdminMenuRepository;
import org.infinity.passport.service.AdminMenuService;
import org.infinity.passport.service.AuthorityAdminMenuService;
import org.infinity.passport.service.AuthorityService;
import org.springframework.data.domain.Example;
import org.springframework.data.domain.ExampleMatcher;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Service;

import java.util.*;
import java.util.function.BiFunction;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AdminMenuServiceImpl implements AdminMenuService {

    private AdminMenuRepository       adminMenuRepository;
    private AuthorityService          authorityService;
    private AuthorityAdminMenuService authorityAdminMenuService;
    private UserDetailsService        userDetailsService;

    @Override
    public Page<AdminMenu> find(Pageable pageable, String appName) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        AdminMenu adminMenu = new AdminMenu();
        adminMenu.setAppName(appName);
        Example<AdminMenu> queryExample = Example.of(adminMenu, matcher);
        return adminMenuRepository.findAll(queryExample, pageable);
    }

    @Override
    public List<AdminMenu> getUserAuthorityLinks(String appName) {
        Set<String> adminMenuIds = getAdminMenuIds(getEnabledUserAuthorities());
        if (CollectionUtils.isEmpty(adminMenuIds)) {
            return Collections.emptyList();
        }
        // 检索二级及以上级别菜单
        return adminMenuRepository.findByAppNameAndIdInAndParentIdNotNull(appName, adminMenuIds);
    }

    @Override
    public List<AdminMenu> getUserAuthorityMenus(String appName) {
        Set<String> adminMenuIds = getAdminMenuIds(getEnabledUserAuthorities());
        if (CollectionUtils.isEmpty(adminMenuIds)) {
            return Collections.emptyList();
        }
        List<AdminMenu> adminMenus = adminMenuRepository.findByAppNameAndIdIn(appName, adminMenuIds);
        return convertToTree(adminMenus);
    }

    @Override
    public List<AdminMenu> getAuthorityMenus(String appName, String authorityName) {
        Set<String> authorityAdminMenuIds = getAdminMenuIds(Collections.singletonList(authorityName));
        if (CollectionUtils.isEmpty(authorityAdminMenuIds)) {
            return Collections.emptyList();
        }
        // 检索所有菜单并将已赋权菜单的checked字段设置为true
        List<AdminMenu> allAdminMenus = adminMenuRepository.findByAppName(appName).stream().peek(menu -> {
            if (authorityAdminMenuIds.contains(menu.getId())) {
                menu.setChecked(true);
            }
        }).collect(Collectors.toList());
        return convertToTree(allAdminMenus);
    }

    private List<String> getEnabledUserAuthorities() {
        List<String> allEnabledAuthorities = authorityService.findAllAuthorityNames(true);
        UserDetails userDetails = userDetailsService.loadUserByUsername(SecurityUtils.getCurrentUserName());
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(allEnabledAuthorities::contains).collect(Collectors.toList());
    }

    private Set<String> getAdminMenuIds(List<String> authorityNames) {
        if (CollectionUtils.isEmpty(authorityNames)) {
            return Collections.emptySet();
        }
        return authorityAdminMenuService.findAdminMenuIds(authorityNames);
    }

    private List<AdminMenu> convertToTree(List<AdminMenu> menus) {
        return convertToTree(menus, "0");
    }

    private List<AdminMenu> convertToTree(List<AdminMenu> menus, String parentId) {
        return menus.stream()
                // filter by parentId
                .filter(parent -> parentId.equals(parent.getParentId()))
                // sort by order
                .sorted(Comparator.comparing(AdminMenu::getSequence))
                // 把父节点children递归赋值成为子节点
                .peek(node -> node.setChildren(convertToTree(menus, node.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void moveUp(String id) {
        this.adjustSeq(id, -1, this::isNotHead);
    }

    private boolean isNotHead(LinkedList<AdminMenu> linkedList, AdminMenu current) {
        return !linkedList.getFirst().equals(current);
    }

    @Override
    public void moveDown(String id) {
        this.adjustSeq(id, 1, this::isNotTail);
    }

    private boolean isNotTail(LinkedList<AdminMenu> linkedList, AdminMenu current) {
        return !linkedList.getLast().equals(current);
    }

    private void adjustSeq(String id, int moveIndex, BiFunction<LinkedList<AdminMenu>, AdminMenu, Boolean> func) {
        AdminMenu current = adminMenuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
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

        // Re-order the sequences
        for (int i = 0; i < linkedList.size(); i++) {
            linkedList.get(i).setSequence(i + 1);
        }
        adminMenuRepository.saveAll(linkedList);
    }
}