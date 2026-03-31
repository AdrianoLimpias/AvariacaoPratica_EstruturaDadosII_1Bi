// src/projeto3/ordenacao/QuickSort.java - VERSÃO CORRIGIDA
package projeto3.ordenacao;

public class QuickSort {

    // Limite para usar insertion sort em subarrays pequenos
    private static final int INSERTION_SORT_THRESHOLD = 10;

    public static void sort(int[] array) {
        quickSort(array, 0, array.length - 1);
    }

    private static void quickSort(int[] array, int baixo, int alto) {
        // Para subarrays pequenos, usar insertion sort (mais eficiente)
        if (alto - baixo <= INSERTION_SORT_THRESHOLD) {
            insertionSort(array, baixo, alto);
            return;
        }

        if (baixo < alto) {
            // Escolher pivô mediana de três para evitar pior caso
            int pi = partitionMedianOfThree(array, baixo, alto);

            quickSort(array, baixo, pi - 1);
            quickSort(array, pi + 1, alto);
        }
    }

    // Partition com mediana de três
    private static int partitionMedianOfThree(int[] array, int baixo, int alto) {
        int meio = baixo + (alto - baixo) / 2;

        // Colocar a mediana no final
        if (array[meio] < array[baixo]) {
            swap(array, baixo, meio);
        }
        if (array[alto] < array[baixo]) {
            swap(array, baixo, alto);
        }
        if (array[alto] < array[meio]) {
            swap(array, meio, alto);
        }

        // Usar a mediana como pivô
        swap(array, meio, alto - 1);
        int pivo = array[alto - 1];

        int i = baixo;
        int j = alto - 2;

        while (true) {
            while (i < alto - 1 && array[++i] < pivo);
            while (j > baixo && array[--j] > pivo);
            if (i >= j) break;
            swap(array, i, j);
        }

        swap(array, i, alto - 1);
        return i;
    }

    // Partition original (mantida para compatibilidade)
    private static int partition(int[] array, int baixo, int alto) {
        // Escolher pivô como mediana de três para evitar pior caso
        int meio = baixo + (alto - baixo) / 2;

        // Ordenar os três elementos
        if (array[meio] < array[baixo]) {
            swap(array, baixo, meio);
        }
        if (array[alto] < array[baixo]) {
            swap(array, baixo, alto);
        }
        if (array[alto] < array[meio]) {
            swap(array, meio, alto);
        }

        // Colocar o pivô (mediana) na posição alto-1
        swap(array, meio, alto - 1);
        int pivo = array[alto - 1];

        int i = baixo;
        int j = alto - 2;

        while (true) {
            while (i < alto - 1 && array[++i] < pivo);
            while (j > baixo && array[--j] > pivo);
            if (i >= j) break;
            swap(array, i, j);
        }

        swap(array, i, alto - 1);
        return i;
    }

    // Insertion sort para subarrays pequenos
    private static void insertionSort(int[] array, int baixo, int alto) {
        for (int i = baixo + 1; i <= alto; i++) {
            int chave = array[i];
            int j = i - 1;
            while (j >= baixo && array[j] > chave) {
                array[j + 1] = array[j];
                j--;
            }
            array[j + 1] = chave;
        }
    }

    private static void swap(int[] array, int i, int j) {
        int temp = array[i];
        array[i] = array[j];
        array[j] = temp;
    }

    public static long sortComTempo(int[] array) {
        int[] copia = array.clone();
        long start = System.nanoTime();
        sort(copia);
        long end = System.nanoTime();
        return end - start;
    }
}