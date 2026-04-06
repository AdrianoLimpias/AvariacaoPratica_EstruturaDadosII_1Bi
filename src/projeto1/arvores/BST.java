// src/projeto1/arvores/BST.java - CORREÇÃO
package projeto1.arvores;

public class BST {
    private class Node {
        int key;
        Node left, right;

        Node(int key) {
            this.key = key;
            left = right = null;
        }
    }

    private Node root;
    private int size; // Adicionado controle de tamanho

    public BST() {
        root = null;
        size = 0;
    }

    // Inserção com retorno para estatísticas
    public boolean insert(int key) {
        if (search(key)) {
            return false; // Elemento já existe
        }
        root = insertRec(root, key);
        size++;
        return true;
    }

    private Node insertRec(Node root, int key) {
        if (root == null) {
            return new Node(key);
        }

        if (key < root.key) {
            root.left = insertRec(root.left, key);
        } else if (key > root.key) {
            root.right = insertRec(root.right, key);
        }

        return root;
    }

    // Busca
    public boolean search(int key) {
        return searchRec(root, key);
    }

    private boolean searchRec(Node root, int key) {
        if (root == null) {
            return false;
        }

        if (key == root.key) {
            return true;
        }

        return key < root.key ? searchRec(root.left, key) : searchRec(root.right, key);
    }

    // Remoção
    public boolean delete(int key) {
        if (!search(key)) {
            return false;
        }
        root = deleteRec(root, key);
        size--;
        return true;
    }

    private Node deleteRec(Node root, int key) {
        if (root == null) {
            return null;
        }

        if (key < root.key) {
            root.left = deleteRec(root.left, key);
        } else if (key > root.key) {
            root.right = deleteRec(root.right, key);
        } else {
            if (root.left == null) {
                return root.right;
            } else if (root.right == null) {
                return root.left;
            }

            Node successor = findMin(root.right);
            root.key = successor.key;
            root.right = deleteRec(root.right, successor.key);
        }

        return root;
    }

    private Node findMin(Node node) {
        while (node.left != null) {
            node = node.left;
        }
        return node;
    }

    // Altura
    public int height() {
        return heightRec(root);
    }

    private int heightRec(Node node) {
        if (node == null) {
            return 0;
        }
        return Math.max(heightRec(node.left), heightRec(node.right)) + 1;
    }

    public int size() {
        return size;
    }

    public boolean isEmpty() {
        return root == null;
    }
}