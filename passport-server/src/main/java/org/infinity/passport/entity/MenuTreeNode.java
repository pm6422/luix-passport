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

    public List<MenuTreeNode> getChildren() {
        return children;
    }


    public MenuTreeNode addChild(MenuTreeNode n) {
        children.add(n);
        return this;
    }
}
