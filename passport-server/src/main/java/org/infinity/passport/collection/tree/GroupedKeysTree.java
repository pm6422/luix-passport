package org.infinity.passport.collection.tree;

import org.apache.commons.collections.CollectionUtils;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.CharUtils;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.Assert;

import java.io.IOException;
import java.io.Serializable;
import java.io.Writer;
import java.util.*;

/**
 * 由于子节点是由数组存储，数据插入涉及到数组拷贝，因此不适合存储千万以上的数据。千万以上数据检索还是很快，但是插入有些慢。
 *
 * @param <T>
 */
public class GroupedKeysTree<T> implements Serializable {

    private static final long                   serialVersionUID = 1L;
    private static final Logger                 LOGGER           = LoggerFactory.getLogger(GroupedKeysTree.class);
    private              int                    nodeSize         = 0;
    private              int                    dataSize         = 0;
    private              int                    depth            = 0;
    private              GroupedKeysTreeNode<T> root             = newNode();

    public GroupedKeysTree() {
        super();
    }

    public int getNodeSize() {
        return nodeSize;
    }

    public int getDataSize() {
        return dataSize;
    }

    public int getDepth() {
        return depth;
    }

    public GroupedKeysTreeNode<T> getRoot() {
        return root;
    }

    private GroupedKeysTreeNode<T> newNode() {
        return new GroupedKeysTreeNode<T>();
    }

    @SuppressWarnings("unchecked")
    private GroupedKeysTreeNode<T>[] newNodeArray(GroupedKeysTreeNode<T>... nodes) {
        return nodes;
    }

    private GroupedKeysTreeNode<T> newChildNode(GroupedKeysTreeNode<T> parentNode, T data, String... keys) {
        if (depth < parentNode.getDepth() + 1) {
            depth = parentNode.getDepth() + 1;
        }

        T storedData = null;
        if (parentNode.getDepth() == keys.length - 1) {
            storedData = data;
            dataSize++;
        }
        return new GroupedKeysTreeNode<T>(keys[parentNode.getDepth()],
                storedData, true, parentNode.getDepth() + 1);
    }

    private String[] removeNullKeys(String... keys) {
        Assert.notEmpty(keys, "keys must NOT be empty");
        return Arrays.stream(keys).filter(s -> StringUtils.isNotEmpty(s)).toArray(String[]::new);
    }

    public void insert(T data, String... keys) {
        this.insert(root, data, removeNullKeys(keys));
    }

    @SuppressWarnings("unchecked")
    private GroupedKeysTreeNode<T> insert(GroupedKeysTreeNode<T> parentNode, T data, String... keys) {
        if (parentNode.getDepth() == keys.length) {
            return parentNode;
        }

        int end = parentNode.getChildren().length;
        if (end == 0) {
            // zero child nodes
            GroupedKeysTreeNode<T> newChildNode = newChildNode(parentNode, data, keys);
            parentNode.setChildren(newNodeArray(newChildNode));
            nodeSize++;
            if (newChildNode.getDepth() < keys.length) {
                insert(newChildNode, data, keys);
            }

            return parentNode;
        }

        // more than one child nodes
        for (int i = 0; i < end; i++) {
            GroupedKeysTreeNode<T> n = parentNode.getChildren()[i];
            int d = keys[parentNode.getDepth()].compareTo(n.getKey());
            if (d == 0) {
                if (n.getDepth() == keys.length) {
                    // add data value
                    n.addData(data);
                    dataSize++;
                } else {
                    // insert node on child node
                    insert(n, data, keys);
                }
                break;
            } else {
                // insert in front of the current node
                int insertIndex = i;
                if (d > 0 && i == end - 1) {
                    // insert into end if it is the last one
                    insertIndex = end;
                }
                if (d < 0 || d > 0 && i == end - 1) {
                    GroupedKeysTreeNode<T> newChildNode = newChildNode(parentNode, data, keys);
                    parentNode.addChild(insertIndex, newChildNode);
                    nodeSize++;
                    if (newChildNode.getDepth() < keys.length) {
                        insert(newChildNode, data, keys);
                    }
                    break;
                }
            }
        }
        return parentNode;
    }

    public List<T> commonPrefixSearch(String... keys) {
        if (nodeSize == 0) {
            return null;
        }

        String[] newKeys = removeNullKeys(keys);
        List<T> ret = new ArrayList<T>();
        GroupedKeysTreeNode<T> node = root;
        for (int i = 0; i < newKeys.length; i++) {
            if (node != null) {
                node = node.getChild(newKeys[i]);
            }
            if (node != null && StringUtils.equals(node.getKey(), newKeys[i]) && CollectionUtils.isNotEmpty(node.getDataSet())) {
                ret.addAll(node.getDataSet());
            }
        }
        return ret;
    }

    public Set<T> preciseSearch(String... keys) {
        if (nodeSize == 0) {
            return null;
        }

        String[] newKeys = removeNullKeys(keys);
        GroupedKeysTreeNode<T> node = root;
        for (int i = 0; i < newKeys.length; i++) {
            if (node != null) {
                node = node.getChild(newKeys[i]);
            }
            if (node != null && StringUtils.equals(node.getKey(), newKeys[i]) && CollectionUtils.isNotEmpty(node.getDataSet())
                    && node.getDepth() == newKeys.length) {
                return node.getDataSet();
            }
        }
        return null;
    }

    public List<T> searchAll() {
        if (nodeSize == 0) {
            return Collections.emptyList();
        }

        return this.searchAll(root);
    }

    private List<T> searchAll(GroupedKeysTreeNode<T> parentNode) {
        List<T> ret = new ArrayList<T>();
        GroupedKeysTreeNode<T> node = parentNode;
        GroupedKeysTreeNode<T>[] children = node.getChildren();
        if (ArrayUtils.isNotEmpty(children)) {
            for (GroupedKeysTreeNode<T> childNode : children) {
                if (CollectionUtils.isNotEmpty(childNode.getDataSet())) {
                    ret.addAll(childNode.getDataSet());
                }
                if (!childNode.isTerminate()) {
                    ret.addAll(searchAll(childNode));
                }
            }
        }
        return ret;
    }

    @SuppressWarnings("unchecked")
    public boolean remove(String... keys) {
        if (nodeSize == 0) {
            return false;
        }

        String[] newKeys = removeNullKeys(keys);
        GroupedKeysTreeNode<T> node = root;
        for (int i = 0; i < newKeys.length; i++) {
            GroupedKeysTreeNode<T> childNode = node.getChild(newKeys[i]);
            if (StringUtils.equals(childNode.getKey(), newKeys[i]) && i == newKeys.length - 1
                    && !childNode.isTerminate()) {
                if (CollectionUtils.isNotEmpty(childNode.getDataSet())) {
                    childNode.setDataSet(Collections.emptySet());
                    dataSize--;
                    return true;
                }
            } else if (StringUtils.equals(childNode.getKey(), newKeys[i]) && i == newKeys.length - 1
                    && childNode.isTerminate()) {
                if (node.getChildren().length == 1) {
                    node.setChildren(newNodeArray());
                    nodeSize--;
                    dataSize--;
                    if (CollectionUtils.isEmpty(node.getDataSet()) && node.getChildren().length == 0) {
                        // also need to remove the parent if there are no children and no data
                        String[] parentKeys = new String[keys.length - 1];
                        System.arraycopy(keys, 0, parentKeys, 0, keys.length - 1);
                        node.setTerminate(true);
                        remove(parentKeys);
                    }
                    return true;
                } else {
                    GroupedKeysTreeNode<T>[] children = node.getChildren();
                    for (int j = 0; j < children.length; j++) {
                        if (StringUtils.equals(children[j].getKey(), newKeys[i])) {
                            node.removeChild(j);
                            nodeSize--;
                            if (CollectionUtils.isNotEmpty(children[j].getDataSet())) {
                                dataSize--;
                            }
                            return true;
                        }
                    }
                }
            }
            node = childNode;
        }
        return false;
    }

    public void update(T data, String... keys) {
        String[] newKeys = removeNullKeys(keys);
        GroupedKeysTreeNode<T> node = root;
        for (int i = 0; i < newKeys.length; i++) {
            node = node.getChild(newKeys[i]);
            if (StringUtils.equals(node.getKey(), newKeys[i]) && CollectionUtils.isNotEmpty(node.getDataSet()) && i == newKeys.length - 1) {
                node.addData(data);
            }
        }
    }

    private StringBuilder output(GroupedKeysTreeNode<T> parentNode) {
        StringBuilder sb = new StringBuilder();
        GroupedKeysTreeNode<T> node = parentNode;
        GroupedKeysTreeNode<T>[] children = node.getChildren();
        if (ArrayUtils.isNotEmpty(children)) {
            for (GroupedKeysTreeNode<T> childNode : children) {
                sb.append(childNode.toString()).append(CharUtils.LF);
                sb.append(output(childNode));
            }
        }
        return sb;
    }

    public void print(Writer writer) throws IOException {
        writer.write(output(root).toString());
    }

    public void print() {
        LOGGER.debug(output(root).toString());
    }
}
