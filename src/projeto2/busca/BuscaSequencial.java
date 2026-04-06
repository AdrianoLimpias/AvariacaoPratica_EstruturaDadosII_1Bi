// src/projeto2/busca/BuscaSequencial.java
package projeto2.busca;

public class BuscaSequencial {

    public static int buscar(int[] array, int alvo) {
        for (int i = 0; i < array.length; i++) {
            if (array[i] == alvo) {
                return i;
            }
        }
        return -1;
    }

    public static long buscarComTempo(int[] array, int alvo) {
        long start = System.nanoTime();
        buscar(array, alvo);
        long end = System.nanoTime();
        return end - start;
    }
}