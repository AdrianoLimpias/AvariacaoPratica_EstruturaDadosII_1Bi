// src/projeto3/ordenacao/HeapSort.java
package projeto3.ordenacao;

public class HeapSort {

    public static void sort(int[] array) {
        int n = array.length;

        // Construir heap
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(array, n, i);
        }

        // Extrair elementos do heap
        for (int i = n - 1; i > 0; i--) {
            // Mover raiz para o fim
            int temp = array[0];
            array[0] = array[i];
            array[i] = temp;

            // Chamar heapify na heap reduzida
            heapify(array, i, 0);
        }
    }

    private static void heapify(int[] array, int n, int i) {
        int maior = i;
        int esquerda = 2 * i + 1;
        int direita = 2 * i + 2;

        if (esquerda < n && array[esquerda] > array[maior]) {
            maior = esquerda;
        }

        if (direita < n && array[direita] > array[maior]) {
            maior = direita;
        }

        if (maior != i) {
            int swap = array[i];
            array[i] = array[maior];
            array[maior] = swap;

            heapify(array, n, maior);
        }
    }

    public static long sortComTempo(int[] array) {
        int[] copia = array.clone();
        long start = System.nanoTime();
        sort(copia);
        long end = System.nanoTime();
        return end - start;
    }
}