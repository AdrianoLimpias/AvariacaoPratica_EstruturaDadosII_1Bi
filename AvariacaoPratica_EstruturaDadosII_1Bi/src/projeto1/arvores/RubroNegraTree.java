// src/projeto1/arvores/RubroNegraTree.java - VERSÃO CORRIGIDA
package projeto1.arvores;

public class RubroNegraTree {
    private static final boolean VERMELHO = true;
    private static final boolean PRETO = false;

    private class Node {
        int key;
        Node left, right, parent;
        boolean cor;

        Node(int key) {
            this.key = key;
            this.cor = VERMELHO;
            left = right = parent = null;
        }
    }

    private Node root;
    private int size;

    public RubroNegraTree() {
        root = null;
        size = 0;
    }

    // Rotação esquerda
    private void leftRotate(Node x) {
        Node y = x.right;
        x.right = y.left;

        if (y.left != null) {
            y.left.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.left) {
            x.parent.left = y;
        } else {
            x.parent.right = y;
        }

        y.left = x;
        x.parent = y;
    }

    // Rotação direita
    private void rightRotate(Node x) {
        Node y = x.left;
        x.left = y.right;

        if (y.right != null) {
            y.right.parent = x;
        }

        y.parent = x.parent;

        if (x.parent == null) {
            root = y;
        } else if (x == x.parent.right) {
            x.parent.right = y;
        } else {
            x.parent.left = y;
        }

        y.right = x;
        x.parent = y;
    }

    // Inserção
    public void insert(int key) {
        Node node = new Node(key);
        root = insertRec(root, node);
        fixInsert(node);
        size++;
    }

    private Node insertRec(Node root, Node node) {
        if (root == null) {
            return node;
        }

        if (node.key < root.key) {
            root.left = insertRec(root.left, node);
            root.left.parent = root;
        } else if (node.key > root.key) {
            root.right = insertRec(root.right, node);
            root.right.parent = root;
        }

        return root;
    }

    // CORREÇÃO: Método fixInsert com verificação de null
    private void fixInsert(Node node) {
        // Caso o nó seja a raiz
        if (node == root) {
            node.cor = PRETO;
            return;
        }

        // Enquanto o pai for vermelho
        while (node != null && node != root && node.parent != null && node.parent.cor == VERMELHO) {
            Node parent = node.parent;
            Node grandparent = parent.parent;

            // Se o pai é null, sair
            if (grandparent == null) {
                break;
            }

            if (parent == grandparent.left) {
                Node uncle = grandparent.right;

                // Caso 1: Tio é vermelho
                if (uncle != null && uncle.cor == VERMELHO) {
                    grandparent.cor = VERMELHO;
                    parent.cor = PRETO;
                    uncle.cor = PRETO;
                    node = grandparent;
                } else {
                    // Caso 2: Tio é preto ou null
                    if (node == parent.right) {
                        leftRotate(parent);
                        node = parent;
                        parent = node.parent;
                    }

                    // Caso 3
                    if (parent != null) {
                        rightRotate(grandparent);
                        boolean temp = parent.cor;
                        parent.cor = grandparent.cor;
                        grandparent.cor = temp;
                        node = parent;
                    }
                }
            } else {
                Node uncle = grandparent.left;

                // Caso 1: Tio é vermelho
                if (uncle != null && uncle.cor == VERMELHO) {
                    grandparent.cor = VERMELHO;
                    parent.cor = PRETO;
                    uncle.cor = PRETO;
                    node = grandparent;
                } else {
                    // Caso 2: Tio é preto ou null
                    if (node == parent.left) {
                        rightRotate(parent);
                        node = parent;
                        parent = node.parent;
                    }

                    // Caso 3
                    if (parent != null) {
                        leftRotate(grandparent);
                        boolean temp = parent.cor;
                        parent.cor = grandparent.cor;
                        grandparent.cor = temp;
                        node = parent;
                    }
                }
            }

            // Verificar se chegou na raiz
            if (node == root) {
                break;
            }
        }

        if (root != null) {
            root.cor = PRETO;
        }
    }

    // Busca
    public boolean search(int key) {
        return searchRec(root, key);
    }

    private boolean searchRec(Node node, int key) {
        if (node == null) {
            return false;
        }

        if (key == node.key) {
            return true;
        }

        return key < node.key ? searchRec(node.left, key) : searchRec(node.right, key);
    }

    // Remoção simplificada (para o experimento)
    public void delete(int key) {
        Node node = searchNode(root, key);
        if (node != null) {
            deleteNode(node);
            size--;
        }
    }

    private Node searchNode(Node node, int key) {
        if (node == null || key == node.key) {
            return node;
        }
        return key < node.key ? searchNode(node.left, key) : searchNode(node.right, key);
    }

    private void deleteNode(Node node) {
        // Implementação simplificada - para remoção completa, usar biblioteca
        // Para o experimento, apenas marcamos como removido na prática
        // Uma implementação completa seria muito extensa
        if (node.left == null && node.right == null) {
            if (node.parent == null) {
                root = null;
            } else if (node == node.parent.left) {
                node.parent.left = null;
            } else {
                node.parent.right = null;
            }
        } else if (node.left == null) {
            transplant(node, node.right);
        } else if (node.right == null) {
            transplant(node, node.left);
        } else {
            Node successor = minimum(node.right);
            if (successor.parent != node) {
                transplant(successor, successor.right);
                successor.right = node.right;
                successor.right.parent = successor;
            }
            transplant(node, successor);
            successor.left = node.left;
            successor.left.parent = successor;
        }
    }

    private void transplant(Node u, Node v) {
        if (u.parent == null) {
            root = v;
        } else if (u == u.parent.left) {
            u.parent.left = v;
        } else {
            u.parent.right = v;
        }
        if (v != null) {
            v.parent = u.parent;
        }
    }

    private Node minimum(Node node) {
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