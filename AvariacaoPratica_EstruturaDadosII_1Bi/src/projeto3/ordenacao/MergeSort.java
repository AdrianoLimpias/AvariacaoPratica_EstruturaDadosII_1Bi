// src/projeto3/ordenacao/MergeSort.java
package projeto3.ordenacao;

public class MergeSort {

    public static void sort(int[] array) {
        mergeSort(array, 0, array.length - 1);
    }

    private static void mergeSort(int[] array, int esquerda, int direita) {
        if (esquerda < direita) {
            int meio = esquerda + (direita - esquerda) / 2;

            mergeSort(array, esquerda, meio);
            mergeSort(array, meio + 1, direita);

            merge(array, esquerda, meio, direita);
        }
    }

    private static void merge(int[] array, int esquerda, int meio, int direita) {
        int n1 = meio - esquerda + 1;
        int n2 = direita - meio;

        int[] L = new int[n1];
        int[] R = new int[n2];

        for (int i = 0; i < n1; i++) {
            L[i] = array[esquerda + i];
        }
        for (int j = 0; j < n2; j++) {
            R[j] = array[meio + 1 + j];
        }

        int i = 0, j = 0;
        int k = esquerda;

        while (i < n1 && j < n2) {
            if (L[i] <= R[j]) {
                array[k] = L[i];
                i++;
            } else {
                array[k] = R[j];
                j++;
            }
            k++;
        }

        while (i < n1) {
            array[k] = L[i];
            i++;
            k++;
        }

        while (j < n2) {
            array[k] = R[j];
            j++;
            k++;
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