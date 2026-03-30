// src/projeto3/ordenacao/InsertionSort.java
package projeto3.ordenacao;

public class InsertionSort {

    public static void sort(int[] array) {
        int n = array.length;

        for (int i = 1; i < n; i++) {
            int chave = array[i];
            int j = i - 1;

            while (j >= 0 && array[j] > chave) {
                array[j + 1] = array[j];
                j--;
            }

            array[j + 1] = chave;
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