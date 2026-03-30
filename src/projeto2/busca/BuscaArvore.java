// src/projeto2/busca/BuscaArvore.java
package projeto2.busca;

import projeto1.arvores.BST;

public class BuscaArvore {

    public static int buscar(BST arvore, int alvo) {
        return arvore.search(alvo) ? 1 : -1;
    }

    public static long buscarComTempo(int[] array, int alvo) {
        BST arvore = new BST();

        // Construir árvore
        for (int val : array) {
            arvore.insert(val);
        }

        long start = System.nanoTime();
        arvore.search(alvo);
        long end = System.nanoTime();

        return end - start;
    }
}