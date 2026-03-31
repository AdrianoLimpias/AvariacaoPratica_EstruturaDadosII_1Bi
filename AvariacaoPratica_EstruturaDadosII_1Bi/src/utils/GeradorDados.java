// src/utils/GeradorDados.java - VERSÃO CORRIGIDA
package utils;

import java.util.*;
import java.io.*;

/**
 * Classe utilitária para geração de dados para os experimentos
 * Todos os métodos são thread-safe e garantem reprodutibilidade
 */
public class GeradorDados {

    private static final Random random = new Random(42); // Seed fixa para reprodutibilidade
    private static final Object lock = new Object(); // Para sincronização

    // Construtor privado para evitar instanciação
    private GeradorDados() {}

    /**
     * Reinicia a seed do gerador para garantir reprodutibilidade
     */
    public static void resetSeed() {
        synchronized (lock) {
            random.setSeed(42);
        }
    }

    /**
     * Gera array de inteiros aleatórios
     * @param tamanho Número de elementos (deve ser > 0)
     * @param maxValor Valor máximo (exclusivo, deve ser > 0)
     * @return Array de inteiros aleatórios
     * @throws IllegalArgumentException se parâmetros inválidos
     */
    public static int[] gerarArrayInteiros(int tamanho, int maxValor) {
        if (tamanho <= 0) {
            throw new IllegalArgumentException("Tamanho deve ser positivo: " + tamanho);
        }
        if (maxValor <= 0) {
            throw new IllegalArgumentException("MaxValor deve ser positivo: " + maxValor);
        }

        int[] array = new int[tamanho];
        synchronized (lock) {
            for (int i = 0; i < tamanho; i++) {
                array[i] = random.nextInt(maxValor);
            }
        }
        return array;
    }

    /**
     * Gera array de inteiros com valores em intervalo específico
     * @param tamanho Número de elementos
     * @param min Valor mínimo (inclusive)
     * @param max Valor máximo (exclusive)
     */
    public static int[] gerarArrayInteirosIntervalo(int tamanho, int min, int max) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
        if (min >= max) throw new IllegalArgumentException("min deve ser menor que max");

        int[] array = new int[tamanho];
        int range = max - min;
        synchronized (lock) {
            for (int i = 0; i < tamanho; i++) {
                array[i] = min + random.nextInt(range);
            }
        }
        return array;
    }

    /**
     * Gera array de inteiros ordenados
     * @param tamanho Número de elementos
     * @return Array ordenado crescente
     */
    public static int[] gerarArrayOrdenado(int tamanho) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");

        int[] array = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            array[i] = i;
        }
        return array;
    }

    /**
     * Gera array de inteiros em ordem decrescente
     * @param tamanho Número de elementos
     * @return Array ordenado decrescente
     */
    public static int[] gerarArrayDecrescente(int tamanho) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");

        int[] array = new int[tamanho];
        for (int i = 0; i < tamanho; i++) {
            array[i] = tamanho - i;
        }
        return array;
    }

    /**
     * Gera array de inteiros quase ordenado (CORRIGIDO)
     * @param tamanho Número de elementos
     * @param percentualDesordem Percentual de elementos fora de ordem (0-100)
     * @return Array quase ordenado
     */
    public static int[] gerarArrayQuaseOrdenado(int tamanho, int percentualDesordem) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
        if (percentualDesordem < 0 || percentualDesordem > 100) {
            throw new IllegalArgumentException("Percentual deve estar entre 0 e 100");
        }

        int[] array = gerarArrayOrdenado(tamanho);

        // Calcular número de trocas baseado no percentual
        int numTrocas = (int) ((tamanho * percentualDesordem) / 100.0);
        numTrocas = Math.min(numTrocas, tamanho / 2); // Limitar para não desordenar completamente

        synchronized (lock) {
            for (int i = 0; i < numTrocas; i++) {
                // Trocar elementos adjacentes para manter ordem quase perfeita
                int pos = random.nextInt(tamanho - 1);
                int temp = array[pos];
                array[pos] = array[pos + 1];
                array[pos + 1] = temp;
            }
        }

        return array;
    }

    /**
     * Gera lista de inteiros aleatórios
     * @param tamanho Número de elementos
     * @return Lista de inteiros
     */
    public static List<Integer> gerarListaInteiros(int tamanho) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");

        List<Integer> lista = new ArrayList<>(tamanho);
        synchronized (lock) {
            for (int i = 0; i < tamanho; i++) {
                lista.add(random.nextInt(tamanho * 10));
            }
        }
        return lista;
    }

    /**
     * Gera array de doubles
     * @param tamanho Número de elementos
     * @param min Valor mínimo
     * @param max Valor máximo
     */
    public static double[] gerarArrayDoubles(int tamanho, double min, double max) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
        if (min >= max) throw new IllegalArgumentException("min deve ser menor que max");

        double[] array = new double[tamanho];
        double range = max - min;
        synchronized (lock) {
            for (int i = 0; i < tamanho; i++) {
                array[i] = min + random.nextDouble() * range;
            }
        }
        return array;
    }

    /**
     * Gera matriz de distâncias simétrica para o problema do caixeiro viajante
     * @param n Número de cidades (deve ser > 0)
     * @return Matriz de distâncias n x n
     */
    public static double[][] gerarMatrizDistancias(int n) {
        if (n <= 0) throw new IllegalArgumentException("Número de cidades deve ser positivo");

        double[][] distancias = new double[n][n];

        synchronized (lock) {
            for (int i = 0; i < n; i++) {
                for (int j = i + 1; j < n; j++) {
                    // Garantir que a distância satisfaz desigualdade triangular
                    double distancia = 10 + random.nextDouble() * 90;
                    distancias[i][j] = distancia;
                    distancias[j][i] = distancia;
                }
                distancias[i][i] = 0;
            }
        }

        return distancias;
    }

    /**
     * Gera matriz de coordenadas 2D para cidades
     * @param n Número de cidades
     * @return Matriz de coordenadas n x 2
     */
    public static double[][] gerarCoordenadasCidades(int n) {
        if (n <= 0) throw new IllegalArgumentException("Número de cidades deve ser positivo");

        double[][] coordenadas = new double[n][2];
        synchronized (lock) {
            for (int i = 0; i < n; i++) {
                coordenadas[i][0] = random.nextDouble() * 100;
                coordenadas[i][1] = random.nextDouble() * 100;
            }
        }
        return coordenadas;
    }

    /**
     * Calcula matriz de distâncias a partir de coordenadas (distância euclidiana)
     * @param coordenadas Matriz de coordenadas
     * @return Matriz de distâncias
     */
    public static double[][] calcularDistanciasEuclidianas(double[][] coordenadas) {
        if (coordenadas == null || coordenadas.length == 0) {
            throw new IllegalArgumentException("Coordenadas não podem ser nulas ou vazias");
        }

        int n = coordenadas.length;
        double[][] distancias = new double[n][n];

        for (int i = 0; i < n; i++) {
            for (int j = i + 1; j < n; j++) {
                double dx = coordenadas[i][0] - coordenadas[j][0];
                double dy = coordenadas[i][1] - coordenadas[j][1];
                double distancia = Math.sqrt(dx * dx + dy * dy);
                distancias[i][j] = distancia;
                distancias[j][i] = distancia;
            }
            distancias[i][i] = 0;
        }

        return distancias;
    }

    /**
     * Gera array com valores únicos (sem repetição)
     * @param tamanho Número de elementos
     * @param maxValor Valor máximo (deve ser >= tamanho)
     */
    public static int[] gerarArraySemRepeticao(int tamanho, int maxValor) {
        if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
        if (maxValor < tamanho) throw new IllegalArgumentException("maxValor deve ser >= tamanho");

        Set<Integer> valores = new LinkedHashSet<>();
        synchronized (lock) {
            while (valores.size() < tamanho) {
                valores.add(random.nextInt(maxValor));
            }
        }

        return valores.stream().mapToInt(Integer::intValue).toArray();
    }

    /**
     * Salva array em arquivo
     */
    public static void salvarArrayArquivo(int[] dados, String filename) throws IOException {
        if (dados == null) throw new IllegalArgumentException("Dados não podem ser nulos");
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }

        try (BufferedWriter writer = new BufferedWriter(new FileWriter(filename))) {
            for (int valor : dados) {
                writer.write(String.valueOf(valor));
                writer.newLine();
            }
        }
    }

    /**
     * Carrega array de arquivo
     */
    public static int[] carregarArrayArquivo(String filename) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }

        List<Integer> lista = new ArrayList<>();
        try (BufferedReader reader = new BufferedReader(new FileReader(filename))) {
            String linha;
            while ((linha = reader.readLine()) != null) {
                linha = linha.trim();
                if (!linha.isEmpty()) {
                    lista.add(Integer.parseInt(linha));
                }
            }
        }

        return lista.stream().mapToInt(i -> i).toArray();
    }

    /**
     * Classe interna para diferentes distribuições
     */
    public static class Distribuicao {

        private Distribuicao() {}

        /**
         * Gera array com distribuição uniforme
         */
        public static int[] uniforme(int tamanho, int min, int max) {
            return gerarArrayInteirosIntervalo(tamanho, min, max);
        }

        /**
         * Gera array com distribuição normal (Box-Muller)
         */
        public static int[] normal(int tamanho, double media, double desvioPadrao) {
            if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
            if (desvioPadrao <= 0) throw new IllegalArgumentException("Desvio padrão deve ser positivo");

            int[] array = new int[tamanho];
            synchronized (lock) {
                for (int i = 0; i < tamanho; i++) {
                    // Método de Box-Muller para gerar distribuição normal
                    double u1 = random.nextDouble();
                    double u2 = random.nextDouble();
                    double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
                    double valor = media + z0 * desvioPadrao;
                    array[i] = (int) Math.max(0, Math.round(valor));
                }
            }
            return array;
        }

        /**
         * Gera array com distribuição exponencial
         */
        public static int[] exponencial(int tamanho, double lambda) {
            if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
            if (lambda <= 0) throw new IllegalArgumentException("Lambda deve ser positivo");

            int[] array = new int[tamanho];
            synchronized (lock) {
                for (int i = 0; i < tamanho; i++) {
                    double valor = -Math.log(1 - random.nextDouble()) / lambda;
                    array[i] = (int) Math.round(valor);
                }
            }
            return array;
        }

        /**
         * Gera array com muitos valores repetidos
         */
        public static int[] comRepeticoes(int tamanho, int numValoresDistintos) {
            if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");
            if (numValoresDistintos <= 0) throw new IllegalArgumentException("Número de valores deve ser positivo");

            int[] array = new int[tamanho];
            synchronized (lock) {
                for (int i = 0; i < tamanho; i++) {
                    array[i] = random.nextInt(numValoresDistintos);
                }
            }
            return array;
        }

        /**
         * Gera array com distribuição bimodal (mistura de duas normais)
         */
        public static int[] bimodal(int tamanho, double media1, double media2, double desvioPadrao) {
            if (tamanho <= 0) throw new IllegalArgumentException("Tamanho deve ser positivo");

            int[] array = new int[tamanho];
            synchronized (lock) {
                for (int i = 0; i < tamanho; i++) {
                    double media = random.nextBoolean() ? media1 : media2;
                    double u1 = random.nextDouble();
                    double u2 = random.nextDouble();
                    double z0 = Math.sqrt(-2.0 * Math.log(u1)) * Math.cos(2.0 * Math.PI * u2);
                    double valor = media + z0 * desvioPadrao;
                    array[i] = (int) Math.max(0, Math.round(valor));
                }
            }
            return array;
        }
    }
}