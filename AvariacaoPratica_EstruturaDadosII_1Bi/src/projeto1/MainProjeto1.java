// src/projeto1/MainProjeto1.java - VERSÃO COM EXPORTAÇÃO DE DADOS
package projeto1;

import projeto1.arvores.*;
import projeto1.caixeiro.*;
import utils.GeradorDados;
import utils.AnaliseEstatistica;
import java.util.*;
import java.io.*;

public class MainProjeto1 {

    private static final int[] TAMANHOS = {100, 1000, 5000};
    private static final int EXECUCOES = 30;
    private static final String DIR_DADOS = "dados/projeto1/";

    public static void main(String[] args) {
        // Criar diretório para salvar os resultados
        criarDiretorio(DIR_DADOS);

        System.out.println("=== PROJETO 1 - ÁRVORES E CAIXEIRO VIAJANTE ===\n");

        // Teste das árvores
        testArvores();

        // Teste do Caixeiro Viajante
        testCaixeiroViajante();

        System.out.println("\n✅ Resultados salvos em: " + DIR_DADOS);
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

    private static void testArvores() {
        System.out.println("--- TESTE DE ÁRVORES ---\n");

        for (int tamanho : TAMANHOS) {
            System.out.println("═".repeat(60));
            System.out.println("TAMANHO: " + tamanho);
            System.out.println("═".repeat(60));

            // Preparar dados
            List<Integer> dadosAleatorios = GeradorDados.gerarListaInteiros(tamanho);
            List<Integer> dadosOrdenados = new ArrayList<>(dadosAleatorios);
            Collections.sort(dadosOrdenados);

            // Testar cada árvore
            testarArvore("BST", new BST(), dadosAleatorios, dadosOrdenados, tamanho);
            testarArvore("AVL", new AVLTree(), dadosAleatorios, dadosOrdenados, tamanho);
            testarArvore("RubroNegra", new RubroNegraTree(), dadosAleatorios, dadosOrdenados, tamanho);

            System.out.println();
        }
    }

    private static void testarArvore(String nome, Object arvore,
                                     List<Integer> dadosAleatorios,
                                     List<Integer> dadosOrdenados,
                                     int tamanho) {
        System.out.println("\n--- " + nome + " ---");

        // Coletar tempos
        List<Long> temposInsercaoAleatoria = new ArrayList<>();
        List<Long> temposInsercaoOrdenada = new ArrayList<>();
        List<Long> temposBusca = new ArrayList<>();
        List<Long> temposRemocao = new ArrayList<>();
        List<Integer> alturas = new ArrayList<>();

        for (int exec = 0; exec < EXECUCOES; exec++) {
            // Teste com dados aleatórios
            long start = System.nanoTime();
            inserirEmArvore(arvore, dadosAleatorios);
            long end = System.nanoTime();
            temposInsercaoAleatoria.add(end - start);

            // Calcular altura
            alturas.add(calcularAltura(arvore));

            // Teste de busca
            int alvo = dadosAleatorios.get(dadosAleatorios.size() / 2);
            start = System.nanoTime();
            buscarEmArvore(arvore, alvo);
            end = System.nanoTime();
            temposBusca.add(end - start);

            // Teste de remoção
            start = System.nanoTime();
            removerDaArvore(arvore, dadosAleatorios);
            end = System.nanoTime();
            temposRemocao.add(end - start);

            // Resetar para próxima execução (criar nova instância)
            if (arvore instanceof BST) {
                arvore = new BST();
            } else if (arvore instanceof AVLTree) {
                arvore = new AVLTree();
            } else if (arvore instanceof RubroNegraTree) {
                arvore = new RubroNegraTree();
            }

            // Teste com dados ordenados (usando nova instância)
            start = System.nanoTime();
            inserirEmArvore(arvore, dadosOrdenados);
            end = System.nanoTime();
            temposInsercaoOrdenada.add(end - start);

            // Resetar novamente
            if (arvore instanceof BST) {
                arvore = new BST();
            } else if (arvore instanceof AVLTree) {
                arvore = new AVLTree();
            } else if (arvore instanceof RubroNegraTree) {
                arvore = new RubroNegraTree();
            }
        }

        // EXPORTAR RESULTADOS PARA CSV
        try {
            // Exportar tempos de inserção aleatória
            Map<String, List<Long>> insercoes = new HashMap<>();
            insercoes.put(nome + "_" + tamanho + "_InsercaoAleatoria", temposInsercaoAleatoria);
            AnaliseEstatistica.exportarCSV(DIR_DADOS + nome + "_" + tamanho + "_insercao_aleatoria.csv", insercoes);

            // Exportar tempos de inserção ordenada
            Map<String, List<Long>> insercoesOrd = new HashMap<>();
            insercoesOrd.put(nome + "_" + tamanho + "_InsercaoOrdenada", temposInsercaoOrdenada);
            AnaliseEstatistica.exportarCSV(DIR_DADOS + nome + "_" + tamanho + "_insercao_ordenada.csv", insercoesOrd);

            // Exportar tempos de busca
            Map<String, List<Long>> buscas = new HashMap<>();
            buscas.put(nome + "_" + tamanho + "_Busca", temposBusca);
            AnaliseEstatistica.exportarCSV(DIR_DADOS + nome + "_" + tamanho + "_busca.csv", buscas);

            // Exportar tempos de remoção
            Map<String, List<Long>> remocoes = new HashMap<>();
            remocoes.put(nome + "_" + tamanho + "_Remocao", temposRemocao);
            AnaliseEstatistica.exportarCSV(DIR_DADOS + nome + "_" + tamanho + "_remocao.csv", remocoes);

            System.out.println("  📊 Dados exportados para: " + DIR_DADOS + nome + "_" + tamanho + "_*.csv");

        } catch (IOException e) {
            System.err.println("  ❌ Erro ao exportar dados: " + e.getMessage());
        }

        // Estatísticas (impressão no console)
        System.out.println("INSERÇÃO (Aleatória):");
        imprimirEstatisticasTempo(temposInsercaoAleatoria);

        System.out.println("INSERÇÃO (Ordenada):");
        imprimirEstatisticasTempo(temposInsercaoOrdenada);

        System.out.println("BUSCA:");
        imprimirEstatisticasTempo(temposBusca);

        System.out.println("REMOÇÃO:");
        imprimirEstatisticasTempo(temposRemocao);

        System.out.println("ALTURA MÉDIA: " + calcularMediaInteiros(alturas));
        System.out.println("ALTURA MÍN/MÁX: " + Collections.min(alturas) + "/" + Collections.max(alturas));
    }

    private static void inserirEmArvore(Object arvore, List<Integer> dados) {
        if (arvore instanceof BST) {
            BST bst = (BST) arvore;
            for (int val : dados) {
                bst.insert(val);
            }
        } else if (arvore instanceof AVLTree) {
            AVLTree avl = (AVLTree) arvore;
            for (int val : dados) {
                avl.insert(val);
            }
        } else if (arvore instanceof RubroNegraTree) {
            RubroNegraTree rn = (RubroNegraTree) arvore;
            for (int val : dados) {
                rn.insert(val);
            }
        }
    }

    private static void buscarEmArvore(Object arvore, int alvo) {
        if (arvore instanceof BST) {
            ((BST) arvore).search(alvo);
        } else if (arvore instanceof AVLTree) {
            ((AVLTree) arvore).search(alvo);
        } else if (arvore instanceof RubroNegraTree) {
            ((RubroNegraTree) arvore).search(alvo);
        }
    }

    private static void removerDaArvore(Object arvore, List<Integer> dados) {
        if (arvore instanceof BST) {
            BST bst = (BST) arvore;
            for (int val : dados) {
                bst.delete(val);
            }
        } else if (arvore instanceof AVLTree) {
            AVLTree avl = (AVLTree) arvore;
            for (int val : dados) {
                avl.delete(val);
            }
        } else if (arvore instanceof RubroNegraTree) {
            RubroNegraTree rn = (RubroNegraTree) arvore;
            for (int val : dados) {
                rn.delete(val);
            }
        }
    }

    private static int calcularAltura(Object arvore) {
        if (arvore instanceof BST) {
            return ((BST) arvore).height();
        } else if (arvore instanceof AVLTree) {
            return ((AVLTree) arvore).height();
        } else if (arvore instanceof RubroNegraTree) {
            return ((RubroNegraTree) arvore).height();
        }
        return 0;
    }

    private static void testCaixeiroViajante() {
        System.out.println("\n\n=== TESTE DO CAIXEIRO VIAJANTE ===\n");

        int[] tamanhosTSP = {10, 20, 30, 50};

        for (int tamanho : tamanhosTSP) {
            System.out.println("═".repeat(60));
            System.out.println("NÚMERO DE CIDADES: " + tamanho);
            System.out.println("═".repeat(60));

            List<Double> custosVMP = new ArrayList<>();
            List<Double> custosIMP = new ArrayList<>();
            List<Long> temposVMP = new ArrayList<>();
            List<Long> temposIMP = new ArrayList<>();

            for (int exec = 0; exec < EXECUCOES; exec++) {
                double[][] distancias = GeradorDados.gerarMatrizDistancias(tamanho);

                // Vizinho Mais Próximo
                long start = System.nanoTime();
                double custo1 = CaixeiroViajante.vizinhoMaisProximo(distancias);
                long end = System.nanoTime();
                custosVMP.add(custo1);
                temposVMP.add(end - start);

                // Inserção Mais Próxima
                start = System.nanoTime();
                double custo2 = CaixeiroViajante.insercaoMaisProxima(distancias);
                end = System.nanoTime();
                custosIMP.add(custo2);
                temposIMP.add(end - start);
            }

            // EXPORTAR RESULTADOS DO TSP
            try {
                // Exportar custos
                Map<String, List<Double>> custosMap = new HashMap<>();
                custosMap.put("VizinhoMaisProximo_" + tamanho, custosVMP);
                custosMap.put("InsercaoMaisProxima_" + tamanho, custosIMP);
                exportarCSVDouble(DIR_DADOS + "tsp_custos_" + tamanho + ".csv", custosMap);

                // Exportar tempos
                Map<String, List<Long>> temposMap = new HashMap<>();
                temposMap.put("VizinhoMaisProximo_Tempo_" + tamanho, temposVMP);
                temposMap.put("InsercaoMaisProxima_Tempo_" + tamanho, temposIMP);
                AnaliseEstatistica.exportarCSV(DIR_DADOS + "tsp_tempos_" + tamanho + ".csv", temposMap);

                System.out.println("  📊 Dados TSP exportados para: " + DIR_DADOS + "tsp_*_" + tamanho + ".csv");

            } catch (IOException e) {
                System.err.println("  ❌ Erro ao exportar dados TSP: " + e.getMessage());
            }

            System.out.println("\nVIZINHO MAIS PRÓXIMO:");
            System.out.println("  Custo:");
            imprimirEstatisticasDouble(custosVMP);
            System.out.println("  Tempo:");
            imprimirEstatisticasTempo(temposVMP);

            System.out.println("\nINSERÇÃO MAIS PRÓXIMA:");
            System.out.println("  Custo:");
            imprimirEstatisticasDouble(custosIMP);
            System.out.println("  Tempo:");
            imprimirEstatisticasTempo(temposIMP);

            // Comparação
            double mediaVMP = calcularMediaDouble(custosVMP);
            double mediaIMP = calcularMediaDouble(custosIMP);
            double melhoria = ((mediaVMP - mediaIMP) / mediaVMP) * 100;

            System.out.printf(Locale.US, "\nComparação: Inserção Mais Próxima é %.2f%% melhor que Vizinho Mais Próximo%n", melhoria);
        }
    }

    // Método auxiliar para exportar dados double
    private static void exportarCSVDouble(String filename, Map<String, List<Double>> dados) throws IOException {
        AnaliseEstatistica.garantirDiretorio(filename);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Cabeçalho
            writer.print("Execucao");
            for (String key : dados.keySet()) {
                writer.print("," + key);
            }
            writer.println();

            // Dados
            int maxSize = dados.values().stream().mapToInt(List::size).max().orElse(0);
            for (int i = 0; i < maxSize; i++) {
                writer.print(i + 1);
                for (List<Double> valores : dados.values()) {
                    if (i < valores.size()) {
                        writer.printf(Locale.US, ",%.2f", valores.get(i));
                    } else {
                        writer.print(",");
                    }
                }
                writer.println();
            }

            System.out.println("  ✅ Arquivo salvo: " + filename);
        }
    }

    private static void imprimirEstatisticasTempo(List<Long> tempos) {
        AnaliseEstatistica.ResultadoEstatistico est = AnaliseEstatistica.calcularEstatisticas(tempos);
        System.out.printf(Locale.US, "  Média: %.3f ms%n", est.media / 1_000_000.0);
        System.out.printf(Locale.US, "  Mediana: %.3f ms%n", est.mediana / 1_000_000.0);
        System.out.printf(Locale.US, "  Desvio Padrão: %.3f ms%n", est.desvioPadrao / 1_000_000.0);
        System.out.printf(Locale.US, "  Mínimo: %.3f ms%n", est.minimo / 1_000_000.0);
        System.out.printf(Locale.US, "  Máximo: %.3f ms%n", est.maximo / 1_000_000.0);
        System.out.printf(Locale.US, "  IC 95%%: [%.3f, %.3f] ms%n",
                (est.media - est.intervaloConfianca95) / 1_000_000.0,
                (est.media + est.intervaloConfianca95) / 1_000_000.0);
    }

    private static void imprimirEstatisticasDouble(List<Double> valores) {
        AnaliseEstatistica.ResultadoEstatistico est = AnaliseEstatistica.calcularEstatisticas(valores);
        System.out.printf(Locale.US, "  Média: %.2f%n", est.media);
        System.out.printf(Locale.US, "  Mediana: %.2f%n", est.mediana);
        System.out.printf(Locale.US, "  Desvio Padrão: %.2f%n", est.desvioPadrao);
        System.out.printf(Locale.US, "  Mínimo: %.2f%n", est.minimo);
        System.out.printf(Locale.US, "  Máximo: %.2f%n", est.maximo);
        System.out.printf(Locale.US, "  IC 95%%: [%.2f, %.2f]%n",
                est.media - est.intervaloConfianca95,
                est.media + est.intervaloConfianca95);
    }

    private static double calcularMediaInteiros(List<Integer> valores) {
        return valores.stream().mapToInt(Integer::intValue).average().orElse(0);
    }

    private static double calcularMediaDouble(List<Double> valores) {
        return valores.stream().mapToDouble(Double::doubleValue).average().orElse(0);
    }
}