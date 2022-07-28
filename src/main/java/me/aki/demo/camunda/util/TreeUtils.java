package me.aki.demo.camunda.util;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.lang.Pair;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.util.*;
import java.util.function.BiConsumer;
import java.util.function.Consumer;
import java.util.function.Function;

@Slf4j
public class TreeUtils {
    private TreeUtils() {
    }

    @Data
    @AllArgsConstructor
    @NoArgsConstructor
    public static class TreeNode<T> {
        private String id;
        private String parentId;
        private T data;
        private List<TreeNode<T>> children;
    }

    public static <T extends TreeNode<?>> void validateTree(List<T> flatNodeList, String parentId, Consumer<T> onAddToTree) {
        validateTree(flatNodeList, TreeNode::getId, TreeNode::getParentId, parentId, onAddToTree);
    }

    /**
     * 验证对象列表是否能组成单颗树
     *
     * @param flatNodeList 对象列表
     * @param getId        从对象获取id
     * @param getParentId  从对象获取父id
     * @param parentId     当前层父id
     * @param onAddToTree  节点有效时的回调
     * @param <T>          对象类型
     */
    public static <T> void validateTree(List<T> flatNodeList, Function<T, String> getId, Function<T, String> getParentId, String parentId, Consumer<T> onAddToTree) {
        if (flatNodeList.isEmpty()) {
            return;
        }
        ArrayList<T> thisLayer = new ArrayList<>();
        LinkedList<T> copy = new LinkedList<>(flatNodeList);
        Iterator<T> iterator = copy.iterator();
        while (iterator.hasNext()) {
            T node = iterator.next();
            if (parentId.equals(getParentId.apply(node))) {
                thisLayer.add(node);
                onAddToTree.accept(node);
                iterator.remove();
            }
        }
        for (T node : thisLayer) {
            validateTree(copy, getId, getParentId, getId.apply(node), onAddToTree);
        }
    }

    /**
     * 获取该节点的所有子节点
     *
     * @param parent          父节点
     * @param getId           从对象获取id
     * @param getChildrenById 按id获取子节点
     * @param setChildren     给对象设置子节点
     * @param <T>             对象类型
     * @return 对象所有子节点树状集合
     */
    public static <T> List<T> getChildren(T parent, Function<T, String> getId, Function<String, List<T>> getChildrenById, BiConsumer<T, List<T>> setChildren) {
        List<T> childrenList = getChildrenById.apply(getId.apply(parent));
        childrenList.forEach(child -> {
            List<T> nextLevelChildren = getChildren(child, getId, getChildrenById, setChildren);
            setChildren.accept(child, nextLevelChildren);
        });
        return childrenList;
    }

    /**
     * 平铺化树
     *
     * @param treeList            树节点列表
     * @param copyAndMoveChildren copy节点对象并把节点对象上的children移除并返回
     * @param <T>                 节点泛型
     * @return 平铺化树
     */
    public static <T> List<T> flatten(List<T> treeList, Function<T, Pair<T, List<T>>> copyAndMoveChildren) {
        if (CollUtil.isEmpty(treeList)) {
            //要求可变类型
            return new ArrayList<>();
        }
        ArrayList<T> thisLevel = new ArrayList<>();
        for (T t : treeList) {
            Pair<T, List<T>> apply = copyAndMoveChildren.apply(t);
            T copy = apply.getKey();
            List<T> thisChildren = apply.getValue();
            List<T> allChildren = flatten(thisChildren, copyAndMoveChildren);
            thisLevel.addAll(allChildren);
            thisLevel.add(copy);
        }
        return thisLevel;
    }

    @Data
    public static class NodeInfo<T> {
        private T node;
        private T parentNode;
        private List<T> siblingNode;
        private Integer currDepth;

        public static <T> NodeInfo<T> of(T node, T parentNode, List<T> siblingNode, Integer currDepth) {
            NodeInfo<T> s = new NodeInfo<>();
            s.setNode(node);
            s.setParentNode(parentNode);
            s.setSiblingNode(siblingNode);
            s.setCurrDepth(currDepth);
            return s;
        }
    }

    /**
     * 遍历树，广度优先
     *
     * @param treeList    兄弟节点列表
     * @param getChildren 获取子节点函数
     * @param onNode      节点操作
     * @param <T>         节点类型
     */
    public static <T> void travelTreeBFS(List<T> treeList, Function<T, List<T>> getChildren, Consumer<NodeInfo<T>> onNode) {
        travelBFS(null, treeList, getChildren, 1, onNode);
    }

    private static <T> void travelBFS(T parent, List<T> treeList, Function<T, List<T>> getChildren, int currLevel, Consumer<NodeInfo<T>> onNode) {
        if (CollUtil.isEmpty(treeList)) {
            return;
        }
        for (T t : treeList) {
            onNode.accept(NodeInfo.of(t, parent, treeList, currLevel));
            travelBFS(t, getChildren.apply(t), getChildren, currLevel + 1, onNode);
        }
    }

    public static <T> List<T> getLeafNode(List<T> treeList, Function<T, List<T>> getChildren) {
        ArrayList<T> leaf = new ArrayList<>();
        travelTreeBFS(treeList, getChildren, ni -> {
            if (CollUtil.isEmpty(getChildren.apply(ni.getNode()))) {
                leaf.add(ni.getNode());
            }
        });
        return leaf;
    }
}
