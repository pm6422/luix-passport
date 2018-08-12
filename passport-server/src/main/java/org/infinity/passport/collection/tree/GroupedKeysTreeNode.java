package org.infinity.passport.collection.tree;

import org.apache.commons.lang3.StringUtils;

import java.io.Serializable;

public class GroupedKeysTreeNode<T> implements Serializable {

    private static final long                     serialVersionUID = 1L;
    private              String                   key;
    private              T                        data;
    private              boolean                  terminate        = true;
    private              int                      depth;
    private              GroupedKeysTreeNode<T>[] children;
    @SuppressWarnings("rawtypes")
    private static       GroupedKeysTreeNode[]    emptyChildren    = {};

    @SuppressWarnings("unchecked")
    public GroupedKeysTreeNode() {
        this(null, null, true, 0, emptyChildren);
    }

    @SuppressWarnings("unchecked")
    public GroupedKeysTreeNode(String key, T data, boolean terminate, int depth) {
        this(key, data, terminate, depth, emptyChildren);
    }

    private GroupedKeysTreeNode(String key, T data, boolean terminate, int depth, GroupedKeysTreeNode<T>[] children) {
        super();
        this.key = key;
        this.data = data;
        this.terminate = terminate;
        this.depth = depth;
        this.children = children;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
    }

    public boolean isTerminate() {
        return terminate;
    }

    public void setTerminate(boolean terminate) {
        this.terminate = terminate;
    }

    public int getDepth() {
        return depth;
    }

    public void setDepth(int depth) {
        this.depth = depth;
    }

    public GroupedKeysTreeNode<T>[] getChildren() {
        return children;
    }

    public void setChildren(GroupedKeysTreeNode<T>[] children) {
        this.children = children;
        this.terminate = false;
    }

    public GroupedKeysTreeNode<T> getChild(String key) {
        int end = children.length;
        if (end > 16) {
            // binary search
            int start = 0;
            while (start < end) {
                int i = (start + end) / 2;
                GroupedKeysTreeNode<T> n = children[i];

                int d = key.compareTo(n.key);
                if (d == 0) {
                    return n;
                }
                if (d < 0) {
                    end = i;
                } else if (start == i) {
                    break;
                } else {
                    start = i;
                }
            }
        } else {
            for (int i = 0; i < end; i++) {
                GroupedKeysTreeNode<T> n = children[i];
                if (StringUtils.equals(n.key, key)) {
                    return n;
                }
            }
        }
        return null;
    }

    public GroupedKeysTreeNode<T> addChild(int index, GroupedKeysTreeNode<T> n) {
        @SuppressWarnings("unchecked")
        GroupedKeysTreeNode<T>[] newNodes = new GroupedKeysTreeNode[children.length + 1];
        System.arraycopy(children, 0, newNodes, 0, index);
        newNodes[index] = n;
        System.arraycopy(children, index, newNodes, index + 1, children.length - index);
        this.children = newNodes;
        return this;
    }

    public GroupedKeysTreeNode<T> removeChild(int index) {
        @SuppressWarnings("unchecked")
        GroupedKeysTreeNode<T>[] newNodes = new GroupedKeysTreeNode[children.length - 1];
        System.arraycopy(children, 0, newNodes, 0, index);
        if (index < children.length - 1) {
            System.arraycopy(children, index + 1, newNodes, index, children.length - index - 1);
        }
        this.children = newNodes;
        return this;
    }

    @Override
    public String toString() {
        return "GroupedKeysTreeNode [key=" + key + ", data=" + data + ", terminate=" + terminate + ", depth=" + depth + ", childrenSize=" + children.length
                + "]";
    }
}
