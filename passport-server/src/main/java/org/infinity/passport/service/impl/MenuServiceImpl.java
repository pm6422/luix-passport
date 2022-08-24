package org.infinity.passport.service.impl;

import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.config.oauth2.SecurityUtils;
import org.infinity.passport.domain.Menu;
import org.infinity.passport.exception.DataNotFoundException;
import org.infinity.passport.repository.MenuRepository;
import org.infinity.passport.service.AuthorityMenuService;
import org.infinity.passport.service.AuthorityService;
import org.infinity.passport.service.MenuService;
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
public class MenuServiceImpl implements MenuService {
    private MenuRepository       menuRepository;
    private AuthorityService     authorityService;
    private AuthorityMenuService authorityMenuService;
    private UserDetailsService   userDetailsService;

    @Override
    public Page<Menu> find(Pageable pageable, String appName) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Menu menu = new Menu();
        menu.setAppId(appName);
        Example<Menu> queryExample = Example.of(menu, matcher);
        return menuRepository.findAll(queryExample, pageable);
    }

    @Override
    public List<Menu> getUserAuthorityLinks(String appName) {
        Set<String> adminMenuIds = getAdminMenuIds(getEnabledUserAuthorities());
        if (CollectionUtils.isEmpty(adminMenuIds)) {
            return Collections.emptyList();
        }
        // 检索二级及以上级别菜单
        return menuRepository.findByAppIdAndIdInAndParentIdNotNull(appName, adminMenuIds);
    }

    @Override
    public List<Menu> getUserAuthorityMenus(String appName) {
        Set<String> adminMenuIds = getAdminMenuIds(getEnabledUserAuthorities());
        if (CollectionUtils.isEmpty(adminMenuIds)) {
            return Collections.emptyList();
        }
        List<Menu> menus = menuRepository.findByAppIdAndIdIn(appName, adminMenuIds);
        return convertToTree(menus);
    }

    @Override
    public List<Menu> getAuthorityMenus(String appName, String authorityName) {
        Set<String> authorityAdminMenuIds = getAdminMenuIds(Collections.singletonList(authorityName));
        if (CollectionUtils.isEmpty(authorityAdminMenuIds)) {
            return Collections.emptyList();
        }
        // 检索所有菜单并将已赋权菜单的checked字段设置为true
        List<Menu> allMenus = menuRepository.findByAppId(appName).stream().peek(menu -> {
            if (authorityAdminMenuIds.contains(menu.getId())) {
                menu.setChecked(true);
            }
        }).collect(Collectors.toList());
        return convertToTree(allMenus);
    }

    private List<String> getEnabledUserAuthorities() {
        List<String> allEnabledAuthorities = authorityService.findAllAuthorityNames(true);
        UserDetails userDetails = userDetailsService.loadUserByUsername(SecurityUtils.getCurrentUsername());
        return userDetails.getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .filter(allEnabledAuthorities::contains).collect(Collectors.toList());
    }

    private Set<String> getAdminMenuIds(List<String> authorityNames) {
        if (CollectionUtils.isEmpty(authorityNames)) {
            return Collections.emptySet();
        }
        return authorityMenuService.findAdminMenuIds(authorityNames);
    }

    private List<Menu> convertToTree(List<Menu> menus) {
        return convertToTree(menus, "0");
    }

    private List<Menu> convertToTree(List<Menu> menus, String parentId) {
        return menus.stream()
                // filter by parentId
                .filter(parent -> parentId.equals(parent.getParentId()))
                // sort by order
                .sorted(Comparator.comparing(Menu::getSequence))
                // 把父节点children递归赋值成为子节点
                .peek(node -> node.setChildren(convertToTree(menus, node.getId())))
                .collect(Collectors.toList());
    }

    @Override
    public void moveUp(String id) {
        this.adjustSeq(id, -1, this::isNotHead);
    }

    private boolean isNotHead(LinkedList<Menu> linkedList, Menu current) {
        return !linkedList.getFirst().equals(current);
    }

    @Override
    public void moveDown(String id) {
        this.adjustSeq(id, 1, this::isNotTail);
    }

    private boolean isNotTail(LinkedList<Menu> linkedList, Menu current) {
        return !linkedList.getLast().equals(current);
    }

    private void adjustSeq(String id, int moveIndex, BiFunction<LinkedList<Menu>, Menu, Boolean> func) {
        Menu current = menuRepository.findById(id).orElseThrow(() -> new DataNotFoundException(id));
        List<Menu> existingList = menuRepository.findByAppIdAndDepthOrderBySequenceAsc(current.getAppId(), current.getDepth());
        if (CollectionUtils.isNotEmpty(existingList) && existingList.size() == 1) {
            return;
        }
        LinkedList<Menu> linkedList = new LinkedList<>(existingList);
        int currentIndex = linkedList.indexOf(current);

        if (func.apply(linkedList, current)) {
            linkedList.remove(currentIndex);
            linkedList.add(currentIndex + moveIndex, current);
        }

        // Re-order the sequences
        for (int i = 0; i < linkedList.size(); i++) {
            linkedList.get(i).setSequence(i + 1);
        }
        menuRepository.saveAll(linkedList);
    }
}