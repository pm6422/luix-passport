package org.infinity.passport.entity;

import org.apache.commons.collections4.CollectionUtils;
import org.infinity.passport.dto.AdminMenuDTO;

import java.io.Serializable;
import java.util.Comparator;
import java.util.List;

public class MenuTree implements Serializable {

    private static final long         serialVersionUID = 1L;
    private              int          nodeSize         = 0;
    private final        MenuTreeNode root             = new MenuTreeNode();

    public MenuTree(List<MenuTreeNode> list) {
        list.sort(Comparator.comparing(MenuTreeNode::getLevel));
        list.forEach(node -> this.insert(root, node));
        this.sort(root, Comparator.comparing(AdminMenuDTO::getSequence));
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public MenuTreeNode getRoot() {
        return root;
    }

    private void insert(MenuTreeNode parentNode, MenuTreeNode node) {
        if (node.getParentId() == null || node.getParentId().equals(parentNode.getId())) {
            parentNode.addChild(node);
            nodeSize++;
        } else {
            for (MenuTreeNode child : parentNode.getChildren()) {
                this.insert(child, node);
            }
        }
    }

    private void sort(MenuTreeNode node, Comparator<MenuTreeNode> comparator) {
        if (CollectionUtils.isNotEmpty(node.getChildren())) {
            node.getChildren().sort(comparator);
            node.getChildren().forEach(child -> this.sort(child, comparator));
        }
    }

    public List<MenuTreeNode> getChildren() {
        return root.getChildren();
    }
}
