// src/projeto2/MainProjeto2.java - VERSÃO COM EXPORTAÇÃO DE DADOS
package projeto2;

import projeto2.busca.*;
import utils.GeradorDados;
import utils.AnaliseEstatistica;
import java.util.*;
import java.io.*;

public class MainProjeto2 {

    private static final int[] TAMANHOS = {1000, 10000, 50000, 100000};
    private static final int EXECUCOES = 30;
    private static final String DIR_DADOS = "dados/projeto2/";

    public static void main(String[] args) {
        criarDiretorio(DIR_DADOS);

        System.out.println("=== PROJETO 2 - SISTEMAS DE BUSCA ===\n");

        // Mapas para acumular todos os resultados
        Map<String, List<Long>> todosTemposSequencialPresente = new LinkedHashMap<>();
        Map<String, List<Long>> todosTemposSequencialAusente = new LinkedHashMap<>();
        Map<String, List<Long>> todosTemposBinariaPresente = new LinkedHashMap<>();
        Map<String, List<Long>> todosTemposBinariaAusente = new LinkedHashMap<>();
        Map<String, List<Long>> todosTemposArvorePresente = new LinkedHashMap<>();
        Map<String, List<Long>> todosTemposArvoreAusente = new LinkedHashMap<>();

        for (int tamanho : TAMANHOS) {
            System.out.println("═".repeat(60));
            System.out.println("TAMANHO DO ARRAY: " + tamanho);
            System.out.println("═".repeat(60));

            // Preparar dados
            int[] dadosAleatorios = GeradorDados.gerarArrayInteiros(tamanho, tamanho * 10);
            int[] dadosOrdenados = dadosAleatorios.clone();
            Arrays.sort(dadosOrdenados);

            // Escolher elementos para busca (presentes e ausentes)
            List<Integer> elementosPresentes = new ArrayList<>();
            List<Integer> elementosAusentes = new ArrayList<>();

            Random rand = new Random(42);
            for (int i = 0; i < EXECUCOES; i++) {
                int index = rand.nextInt(tamanho);
                elementosPresentes.add(dadosAleatorios[index]);
                elementosAusentes.add(tamanho * 10 + i);
            }

            // Testar algoritmos e acumular resultados
            testarBuscaSequencial(dadosAleatorios, elementosPresentes, elementosAusentes,
                    tamanho, todosTemposSequencialPresente, todosTemposSequencialAusente);
            testarBuscaBinaria(dadosOrdenados, elementosPresentes, elementosAusentes,
                    tamanho, todosTemposBinariaPresente, todosTemposBinariaAusente);
            testarBuscaArvore(dadosAleatorios, elementosPresentes, elementosAusentes,
                    tamanho, todosTemposArvorePresente, todosTemposArvoreAusente);

            System.out.println();
        }

        // EXPORTAR TODOS OS RESULTADOS
        try {
            exportarResultadosBusca("sequencial", todosTemposSequencialPresente, todosTemposSequencialAusente);
            exportarResultadosBusca("binaria", todosTemposBinariaPresente, todosTemposBinariaAusente);
            exportarResultadosBusca("arvore", todosTemposArvorePresente, todosTemposArvoreAusente);

            System.out.println("\n✅ Resultados salvos em: " + DIR_DADOS);
        } catch (IOException e) {
            System.err.println("❌ Erro ao exportar resultados: " + e.getMessage());
        }
    }

    private static void criarDiretorio(String path) {
        File dir = new File(path);
        if (!dir.exists()) {
            boolean criado = dir.mkdirs();
            if (criado) {
                System.out.println("📁 Diretório criado: " + path);
            }
        }
    }

    private static void exportarResultadosBusca(String nome,
                                                Map<String, List<Long>> presentes,
                                                Map<String, List<Long>> ausentes) throws IOException {
        if (!presentes.isEmpty()) {
            AnaliseEstatistica.exportarCSV(DIR_DADOS + "busca_" + nome + "_presente.csv", presentes);
        }
        if (!ausentes.isEmpty()) {
            AnaliseEstatistica.exportarCSV(DIR_DADOS + "busca_" + nome + "_ausente.csv", ausentes);
        }
    }

    private static void testarBuscaSequencial(int[] dados, List<Integer> presentes, List<Integer> ausentes,
                                              int tamanho, Map<String, List<Long>> acumPresentes,
                                              Map<String, List<Long>> acumAusentes) {
        System.out.println("\n--- BUSCA SEQUENCIAL (O(n)) ---");

        List<Long> temposPresentes = new ArrayList<>();
        List<Long> temposAusentes = new ArrayList<>();

        for (int i = 0; i < EXECUCOES; i++) {
            long tempo = BuscaSequencial.buscarComTempo(dados, presentes.get(i));
            temposPresentes.add(tempo);

            tempo = BuscaSequencial.buscarComTempo(dados, ausentes.get(i));
            temposAusentes.add(tempo);
        }

        // Acumular para exportação
        acumPresentes.put("tamanho_" + tamanho, temposPresentes);
        acumAusentes.put("tamanho_" + tamanho, temposAusentes);

        System.out.println("Busca de elemento presente:");
        imprimirEstatisticas(temposPresentes);

        System.out.println("Busca de elemento ausente:");
        imprimirEstatisticas(temposAusentes);
    }

    private static void testarBuscaBinaria(int[] dados, List<Integer> presentes, List<Integer> ausentes,
                                           int tamanho, Map<String, List<Long>> acumPresentes,
                                           Map<String, List<Long>> acumAusentes) {
        System.out.println("\n--- BUSCA BINÁRIA (O(log n)) ---");

        List<Long> temposPresentes = new ArrayList<>();
        List<Long> temposAusentes = new ArrayList<>();

        for (int i = 0; i < EXECUCOES; i++) {
            long tempo = BuscaBinaria.buscarComTempo(dados, presentes.get(i));
            temposPresentes.add(tempo);

            tempo = BuscaBinaria.buscarComTempo(dados, ausentes.get(i));
            temposAusentes.add(tempo);
        }

        // Acumular para exportação
        acumPresentes.put("tamanho_" + tamanho, temposPresentes);
        acumAusentes.put("tamanho_" + tamanho, temposAusentes);

        System.out.println("Busca de elemento presente:");
        imprimirEstatisticas(temposPresentes);

        System.out.println("Busca de elemento ausente:");
        imprimirEstatisticas(temposAusentes);
    }

    private static void testarBuscaArvore(int[] dados, List<Integer> presentes, List<Integer> ausentes,
                                          int tamanho, Map<String, List<Long>> acumPresentes,
                                          Map<String, List<Long>> acumAusentes) {
        System.out.println("\n--- BUSCA EM ÁRVORE (O(log n)) ---");

        List<Long> temposPresentes = new ArrayList<>();
        List<Long> temposAusentes = new ArrayList<>();

        for (int i = 0; i < EXECUCOES; i++) {
            long tempo = BuscaArvore.buscarComTempo(dados, presentes.get(i));
            temposPresentes.add(tempo);

            tempo = BuscaArvore.buscarComTempo(dados, ausentes.get(i));
            temposAusentes.add(tempo);
        }

        // Acumular para exportação
        acumPresentes.put("tamanho_" + tamanho, temposPresentes);
        acumAusentes.put("tamanho_" + tamanho, temposAusentes);

        System.out.println("Busca de elemento presente:");
        imprimirEstatisticas(temposPresentes);

        System.out.println("Busca de elemento ausente:");
        imprimirEstatisticas(temposAusentes);
    }

    private static void imprimirEstatisticas(List<Long> tempos) {
        AnaliseEstatistica.ResultadoEstatistico est = AnaliseEstatistica.calcularEstatisticas(tempos);
        System.out.printf(Locale.US, "  Média: %.3f µs%n", est.media / 1000.0);
        System.out.printf(Locale.US, "  Mediana: %.3f µs%n", est.mediana / 1000.0);
        System.out.printf(Locale.US, "  Desvio Padrão: %.3f µs%n", est.desvioPadrao / 1000.0);
        System.out.printf(Locale.US, "  Mínimo: %.3f µs%n", est.minimo / 1000.0);
        System.out.printf(Locale.US, "  Máximo: %.3f µs%n", est.maximo / 1000.0);
        System.out.printf(Locale.US, "  IC 95%%: [%.3f, %.3f] µs%n",
                (est.media - est.intervaloConfianca95) / 1000.0,
                (est.media + est.intervaloConfianca95) / 1000.0);
    }
}