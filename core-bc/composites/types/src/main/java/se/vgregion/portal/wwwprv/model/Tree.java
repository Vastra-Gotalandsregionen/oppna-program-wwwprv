package se.vgregion.portal.wwwprv.model;

import java.util.ArrayList;
import java.util.List;

public class Tree<T> {
    private Node<T> root;

    public Tree(T rootData) {
        root = new Node<T>();
        root.data = rootData;
        root.children = new ArrayList<Node<T>>();
    }

    public Node<T> getRoot() {
        return root;
    }

    /*public void addNode(T data) {
        root.children.add();
    }*/

    public static class Node<T> {
        private T data;
        private Node<T> parent;
        private List<Node<T>> children = new ArrayList<>();

        public Node() {
        }

        public Node(T data) {
            this.data = data;
        }

        public T getData() {
            return data;
        }

        public void setData(T data) {
            this.data = data;
        }

        public List<Node<T>> getChildren() {
            return children;
        }

        public void setChildren(List<Node<T>> children) {
            this.children = children;
        }

        public void setParent(Node<T> parent) {
            this.parent = parent;
        }

        public Node<T> getParent() {
            return parent;
        }
    }
}