// src/projeto2/busca/BuscaBinaria.java
package projeto2.busca;

import java.util.Arrays;

public class BuscaBinaria {

    public static int buscar(int[] array, int alvo) {
        int esquerda = 0;
        int direita = array.length - 1;

        while (esquerda <= direita) {
            int meio = esquerda + (direita - esquerda) / 2;

            if (array[meio] == alvo) {
                return meio;
            }

            if (array[meio] < alvo) {
                esquerda = meio + 1;
            } else {
                direita = meio - 1;
            }
        }

        return -1;
    }

    public static long buscarComTempo(int[] array, int alvo) {
        // Para busca binária, precisamos que o array esteja ordenado
        int[] arrayOrdenado = array.clone();
        Arrays.sort(arrayOrdenado);

        long start = System.nanoTime();
        buscar(arrayOrdenado, alvo);
        long end = System.nanoTime();

        return end - start;
    }
}