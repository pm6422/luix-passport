package org.infinity.passport.entity;

import org.infinity.passport.dto.AdminMenuDTO;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MenuTreeNode extends AdminMenuDTO implements Serializable {

    private static final long               serialVersionUID = 1L;
    private              List<MenuTreeNode> children         = new ArrayList<>();

    public MenuTreeNode() {
        super();
    }

    private MenuTreeNode(String id, String appName, String name, String label, Integer level, String url,
                         Integer sequence, String parentId, boolean checked, List<MenuTreeNode> children) {
        super();
        setId(id);
        setAppName(appName);
        setName(name);
        setLabel(label);
        setLevel(level);
        setUrl(url);
        setSequence(sequence);
        setParentId(parentId);
        setChecked(false);
        this.children = children;
    }

    public List<MenuTreeNode> getChildren() {
        return children;
    }


    public MenuTreeNode addChild(MenuTreeNode n) {
        children.add(n);
        return this;
    }
}
