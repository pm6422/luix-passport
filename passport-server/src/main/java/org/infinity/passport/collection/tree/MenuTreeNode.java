package org.infinity.passport.collection.tree;

import org.infinity.passport.domain.AdminMenu;
import org.springframework.beans.BeanUtils;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

public class MenuTreeNode extends AdminMenu implements Serializable {

    private static final long               serialVersionUID = 1L;
    private              boolean            terminate        = true;
    private              List<MenuTreeNode> children         = new ArrayList<>();

    public MenuTreeNode() {
        this(null, null, null, null, null, null,
                null, null, true, 0, new ArrayList<>());
    }

    private MenuTreeNode(String id, String appName, String name, String label, Integer level, String url,
                         Integer sequence, String parentId, boolean terminate, int depth, List<MenuTreeNode> children) {
        super();
        setId(id);
        setAppName(appName);
        setName(name);
        setLabel(label);
        setLevel(level);
        setUrl(url);
        setSequence(sequence);
        setParentId(parentId);
        this.terminate = terminate;
        this.children = children;
    }

    public static MenuTreeNode of(AdminMenu menu) {
        MenuTreeNode dest = new MenuTreeNode();
        BeanUtils.copyProperties(menu, dest);
        return dest;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public List<MenuTreeNode> getChildren() {
        return children;
    }

    public void setChildren(List<MenuTreeNode> children) {
        this.children = children;
        this.terminate = false;
    }

    public MenuTreeNode addChild(MenuTreeNode n) {
        children.add(n);
        children.sort(Comparator.comparing(node -> node.getSequence()));
        return this;
    }
}
