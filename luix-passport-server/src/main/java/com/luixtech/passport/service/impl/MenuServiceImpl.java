package com.luixtech.passport.service.impl;

import com.luixtech.passport.config.oauth2.SecurityUtils;
import com.luixtech.passport.domain.Menu;
import com.luixtech.passport.exception.DataNotFoundException;
import com.luixtech.passport.repository.MenuRepository;
import com.luixtech.passport.service.AuthorityMenuService;
import com.luixtech.passport.service.AuthorityService;
import com.luixtech.passport.service.MenuService;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.CollectionUtils;
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

import static com.luixtech.passport.domain.Menu.EMPTY_MENU_ID;

@Service
@AllArgsConstructor
public class MenuServiceImpl implements MenuService {
    private final MenuRepository       menuRepository;
    private final AuthorityService     authorityService;
    private final AuthorityMenuService authorityMenuService;
    private final UserDetailsService   userDetailsService;

    @Override
    public Page<Menu> find(Pageable pageable, String appId, Integer depth) {
        // Ignore query parameter if it has a null value
        ExampleMatcher matcher = ExampleMatcher.matching().withIgnoreNullValues();
        Menu menu = new Menu();
        menu.setAppId(appId);
        menu.setDepth(depth);
        Example<Menu> queryExample = Example.of(menu, matcher);
        return menuRepository.findAll(queryExample, pageable);
    }

    @Override
    public List<Menu> getUserAuthorityLinks(String appName) {
        Set<String> menuIds = getMenuIds(getEnabledUserAuthorities());
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        // 检索二级及以上级别菜单
        return menuRepository.findByAppNameAndIdInAndEnabledIsTrueAndParentIdNotNull(appName, menuIds);
    }

    @Override
    public List<Menu> getUserAuthorityMenus(String appName) {
        Set<String> menuIds = getMenuIds(getEnabledUserAuthorities());
        if (CollectionUtils.isEmpty(menuIds)) {
            return Collections.emptyList();
        }
        List<Menu> menus = menuRepository.findByAppNameAndIdInAndEnabledIsTrue(appName, menuIds);
        return convertToTree(menus);
    }

    @Override
    public List<Menu> getAuthorityMenus(String appId, String authorityName) {
        Set<String> authorityMenuIds = getMenuIds(Collections.singletonList(authorityName));
        // 检索所有菜单并将已赋权菜单的checked字段设置为true
        List<Menu> allMenus = menuRepository.findByAppId(appId)
                .stream()
                .peek(menu -> {
                    if (authorityMenuIds.contains(menu.getId())) {
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

    private Set<String> getMenuIds(List<String> authorityNames) {
        if (CollectionUtils.isEmpty(authorityNames)) {
            return Collections.emptySet();
        }
        return authorityMenuService.findAdminMenuIds(authorityNames);
    }

    private List<Menu> convertToTree(List<Menu> menus) {
        return convertToTree(menus, EMPTY_MENU_ID);
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