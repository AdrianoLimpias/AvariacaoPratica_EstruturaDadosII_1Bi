// src/projeto3/ordenacao/BubbleSort.java
package projeto3.ordenacao;

public class BubbleSort {

    public static void sort(int[] array) {
        int n = array.length;
        boolean trocou;

        for (int i = 0; i < n - 1; i++) {
            trocou = false;

            for (int j = 0; j < n - i - 1; j++) {
                if (array[j] > array[j + 1]) {
                    // Trocar
                    int temp = array[j];
                    array[j] = array[j + 1];
                    array[j + 1] = temp;
                    trocou = true;
                }
            }

            if (!trocou) {
                break;
            }
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