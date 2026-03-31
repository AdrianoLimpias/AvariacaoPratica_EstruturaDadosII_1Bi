// src/projeto3/MainProjeto3.java - VERSÃO CORRIGIDA
package projeto3;

import projeto3.ordenacao.*;
import utils.GeradorDados;
import utils.AnaliseEstatistica;
import java.util.*;

public class MainProjeto3 {

    private static final int[] TAMANHOS = {1000, 5000, 10000, 50000};
    private static final int EXECUCOES = 30;

    public static void main(String[] args) {
        System.out.println("=== PROJETO 3 - BENCHMARK DE ORDENAÇÃO ===\n");

        for (int tamanho : TAMANHOS) {
            System.out.println("═".repeat(60));
            System.out.println("TAMANHO DO ARRAY: " + tamanho);
            System.out.println("═".repeat(60));

            // Gerar diferentes tipos de arrays
            int[] aleatorio = GeradorDados.gerarArrayInteiros(tamanho, tamanho * 10);
            int[] ordenado = GeradorDados.gerarArrayOrdenado(tamanho);
            int[] decrescente = GeradorDados.gerarArrayDecrescente(tamanho);
            int[] quaseOrdenado = GeradorDados.gerarArrayQuaseOrdenado(tamanho, 10);

            // Testar casos
            System.out.println("\n--- MELHOR CASO (Array Ordenado) ---");
            testarAlgoritmos(ordenado, tamanho);

            System.out.println("\n--- CASO MÉDIO (Array Aleatório) ---");
            testarAlgoritmos(aleatorio, tamanho);

            System.out.println("\n--- PIOR CASO (Array Decrescente) ---");
            testarAlgoritmos(decrescente, tamanho);

            System.out.println("\n--- CASO QUASE ORDENADO (10% fora de ordem) ---");
            testarAlgoritmos(quaseOrdenado, tamanho);

            System.out.println();
        }
    }

    // src/projeto3/MainProjeto3.java - PARTE CORRIGIDA
// Apenas a parte que lida com Quick Sort precisa ser ajustada

    private static void testarAlgoritmos(int[] baseArray, int tamanho) {
        Map<String, List<Long>> resultados = new LinkedHashMap<>();

        // Bubble Sort - O(n²)
        if (tamanho <= 10000) {
            List<Long> tempos = testarAlgoritmo(baseArray, (arr) -> BubbleSort.sortComTempo(arr));
            if (!tempos.isEmpty()) {
                resultados.put("Bubble Sort", tempos);
            }
        } else {
            System.out.println("Bubble Sort: Pulando (muito lento para este tamanho)");
        }

        // Insertion Sort - O(n²)
        if (tamanho <= 50000) {
            List<Long> tempos = testarAlgoritmo(baseArray, (arr) -> InsertionSort.sortComTempo(arr));
            if (!tempos.isEmpty()) {
                resultados.put("Insertion Sort", tempos);
            }
        } else {
            System.out.println("Insertion Sort: Pulando (muito lento para este tamanho)");
        }

        // Merge Sort - O(n log n)
        List<Long> temposMerge = testarAlgoritmo(baseArray, (arr) -> MergeSort.sortComTempo(arr));
        if (!temposMerge.isEmpty()) {
            resultados.put("Merge Sort", temposMerge);
        }

        // Quick Sort - O(n log n) - CORREÇÃO: limitar tamanho para evitar StackOverflow
        if (tamanho <= 50000) {
            List<Long> temposQuick = testarAlgoritmo(baseArray, (arr) -> QuickSort.sortComTempo(arr));
            if (!temposQuick.isEmpty()) {
                resultados.put("Quick Sort", temposQuick);
            }
        } else {
            System.out.println("Quick Sort: Pulando para evitar StackOverflow (tamanho muito grande)");
        }

        // Heap Sort - O(n log n)
        List<Long> temposHeap = testarAlgoritmo(baseArray, (arr) -> HeapSort.sortComTempo(arr));
        if (!temposHeap.isEmpty()) {
            resultados.put("Heap Sort", temposHeap);
        }

        // Imprimir comparação
        imprimirComparacao(resultados);

        // Teste de significância estatística
        if (resultados.containsKey("Quick Sort") && resultados.containsKey("Merge Sort")) {
            double[] tResult = AnaliseEstatistica.testeTStudent(
                    resultados.get("Quick Sort"),
                    resultados.get("Merge Sort")
            );
            System.out.printf(Locale.US, "\nTeste t entre Quick Sort e Merge Sort: t = %.3f, p-valor = %.4f%n",
                    tResult[0], tResult[1]);
            if (tResult[1] < 0.05) {
                System.out.println("Diferença estatisticamente significativa (p < 0.05)");
            } else {
                System.out.println("Diferença não é estatisticamente significativa (p >= 0.05)");
            }
        }
    }

    private static List<Long> testarAlgoritmo(int[] baseArray, SortFunction sortFunc) {
        List<Long> tempos = new ArrayList<>();

        for (int i = 0; i < EXECUCOES; i++) {
            try {
                int[] copia = baseArray.clone();
                long start = System.nanoTime();
                sortFunc.sort(copia);
                long end = System.nanoTime();
                tempos.add(end - start);
            } catch (Exception e) {
                System.err.println("Erro na execução: " + e.getMessage());
                // Não adicionar tempo em caso de erro
            }
        }

        return tempos;
    }

    private static void imprimirComparacao(Map<String, List<Long>> resultados) {
        if (resultados == null || resultados.isEmpty()) {
            System.out.println("Nenhum resultado para comparar");
            return;
        }

        System.out.println("\n" + "=".repeat(100));
        System.out.println("COMPARAÇÃO ENTRE ALGORITMOS");
        System.out.println("=".repeat(100));

        System.out.printf("%-20s %15s %15s %15s %15s %15s%n",
                "Algoritmo", "Média (ms)", "DP (ms)", "Min (ms)", "Max (ms)", "CV");
        System.out.println("-".repeat(100));

        for (Map.Entry<String, List<Long>> entry : resultados.entrySet()) {
            AnaliseEstatistica.ResultadoEstatistico est = AnaliseEstatistica.calcularEstatisticas(entry.getValue());
            double cv = est.desvioPadrao / est.media;

            System.out.printf(Locale.US, "%-20s %15.3f %15.3f %15.3f %15.3f %14.3f%n",
                    entry.getKey(),
                    est.media / 1_000_000.0,
                    est.desvioPadrao / 1_000_000.0,
                    est.minimo / 1_000_000.0,
                    est.maximo / 1_000_000.0,
                    cv);
        }
        System.out.println("=".repeat(100));
    }

    @FunctionalInterface
    interface SortFunction {
        void sort(int[] array);
    }
}