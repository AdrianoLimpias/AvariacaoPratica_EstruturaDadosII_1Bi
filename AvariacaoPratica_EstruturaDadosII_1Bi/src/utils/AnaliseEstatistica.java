// src/utils/AnaliseEstatistica.java - VERSÃO COMPLETA COM TODOS OS MÉTODOS
package utils;

import java.util.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.File;

/**
 * Classe utilitária para análise estatística dos experimentos
 */
public class AnaliseEstatistica {

    private static final double Z_95 = 1.96;
    private static final double Z_99 = 2.576;

    private AnaliseEstatistica() {}

    public static class ResultadoEstatistico {
        public double media;
        public double mediana;
        public double desvioPadrao;
        public double variancia;
        public double minimo;
        public double maximo;
        public double amplitude;
        public double intervaloConfianca95;
        public double intervaloConfianca99;
        public double assimetria;
        public double curtose;
        public int tamanhoAmostra;

        public ResultadoEstatistico() {
            this.media = 0;
            this.mediana = 0;
            this.desvioPadrao = 0;
            this.variancia = 0;
            this.minimo = 0;
            this.maximo = 0;
            this.amplitude = 0;
            this.intervaloConfianca95 = 0;
            this.intervaloConfianca99 = 0;
            this.assimetria = 0;
            this.curtose = 0;
            this.tamanhoAmostra = 0;
        }

        @Override
        public String toString() {
            return String.format(Locale.US,
                    "Estatísticas Descritivas:\n" +
                            "  Média: %.4f\n" +
                            "  Mediana: %.4f\n" +
                            "  Desvio Padrão: %.4f\n" +
                            "  Variância: %.4f\n" +
                            "  Mínimo: %.4f\n" +
                            "  Máximo: %.4f\n" +
                            "  Amplitude: %.4f\n" +
                            "  Assimetria: %.4f\n" +
                            "  Curtose: %.4f\n" +
                            "  IC 95%%: [%.4f, %.4f]\n" +
                            "  IC 99%%: [%.4f, %.4f]\n" +
                            "  N: %d",
                    media, mediana, desvioPadrao, variancia, minimo, maximo, amplitude,
                    assimetria, curtose,
                    media - intervaloConfianca95, media + intervaloConfianca95,
                    media - intervaloConfianca99, media + intervaloConfianca99,
                    tamanhoAmostra
            );
        }

        public String toCSV() {
            return String.format(Locale.US,
                    "%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d",
                    media, mediana, desvioPadrao, variancia, minimo, maximo, amplitude,
                    assimetria, curtose, intervaloConfianca95, intervaloConfianca99,
                    tamanhoAmostra
            );
        }
    }

    public static ResultadoEstatistico calcularEstatisticas(List<? extends Number> valores) {
        ResultadoEstatistico resultado = new ResultadoEstatistico();

        if (valores == null || valores.isEmpty()) {
            resultado.tamanhoAmostra = 0;
            return resultado;
        }

        List<Double> dados = new ArrayList<>();
        for (Number n : valores) {
            if (n != null) {
                dados.add(n.doubleValue());
            }
        }

        if (dados.isEmpty()) {
            resultado.tamanhoAmostra = 0;
            return resultado;
        }

        Collections.sort(dados);

        resultado.tamanhoAmostra = dados.size();
        resultado.minimo = dados.get(0);
        resultado.maximo = dados.get(dados.size() - 1);
        resultado.amplitude = resultado.maximo - resultado.minimo;

        double soma = 0;
        for (double d : dados) {
            soma += d;
        }
        resultado.media = soma / dados.size();

        if (dados.size() % 2 == 0) {
            resultado.mediana = (dados.get(dados.size() / 2 - 1) + dados.get(dados.size() / 2)) / 2;
        } else {
            resultado.mediana = dados.get(dados.size() / 2);
        }

        double somaQuadrados = 0;
        for (double d : dados) {
            somaQuadrados += Math.pow(d - resultado.media, 2);
        }
        resultado.variancia = somaQuadrados / dados.size();
        resultado.desvioPadrao = Math.sqrt(resultado.variancia);

        if (resultado.desvioPadrao > 0) {
            double somaCubo = 0;
            for (double d : dados) {
                somaCubo += Math.pow(d - resultado.media, 3);
            }
            resultado.assimetria = somaCubo / (dados.size() * Math.pow(resultado.desvioPadrao, 3));

            double somaQuarta = 0;
            for (double d : dados) {
                somaQuarta += Math.pow(d - resultado.media, 4);
            }
            resultado.curtose = somaQuarta / (dados.size() * Math.pow(resultado.desvioPadrao, 4)) - 3;
        }

        double erroPadrao = resultado.desvioPadrao / Math.sqrt(dados.size());
        resultado.intervaloConfianca95 = Z_95 * erroPadrao;
        resultado.intervaloConfianca99 = Z_99 * erroPadrao;

        return resultado;
    }

    public static ResultadoEstatistico calcularEstatisticas(double[] valores) {
        if (valores == null) return new ResultadoEstatistico();
        List<Double> lista = new ArrayList<>();
        for (double v : valores) lista.add(v);
        return calcularEstatisticas(lista);
    }

    public static ResultadoEstatistico calcularEstatisticas(long[] valores) {
        if (valores == null) return new ResultadoEstatistico();
        List<Long> lista = new ArrayList<>();
        for (long v : valores) lista.add(v);
        return calcularEstatisticas(lista);
    }

    public static ResultadoEstatistico calcularEstatisticas(int[] valores) {
        if (valores == null) return new ResultadoEstatistico();
        List<Integer> lista = new ArrayList<>();
        for (int v : valores) lista.add(v);
        return calcularEstatisticas(lista);
    }

    /**
     * Realiza teste t de Student para duas amostras (assumindo variâncias diferentes)
     * @return array com [valor t, p-valor, graus de liberdade]
     */
    public static double[] testeTStudent(List<? extends Number> amostra1, List<? extends Number> amostra2) {
        if (amostra1 == null || amostra2 == null || amostra1.isEmpty() || amostra2.isEmpty()) {
            return new double[]{0, 1, 0};
        }

        ResultadoEstatistico est1 = calcularEstatisticas(amostra1);
        ResultadoEstatistico est2 = calcularEstatisticas(amostra2);

        double var1 = Math.pow(est1.desvioPadrao, 2);
        double var2 = Math.pow(est2.desvioPadrao, 2);

        double erroPadrao = Math.sqrt(var1 / est1.tamanhoAmostra + var2 / est2.tamanhoAmostra);
        double t = (est1.media - est2.media) / erroPadrao;

        // Graus de liberdade aproximados (Welch–Satterthwaite)
        double dfNumerador = Math.pow(var1 / est1.tamanhoAmostra + var2 / est2.tamanhoAmostra, 2);
        double dfDenominador = Math.pow(var1 / est1.tamanhoAmostra, 2) / (est1.tamanhoAmostra - 1) +
                Math.pow(var2 / est2.tamanhoAmostra, 2) / (est2.tamanhoAmostra - 1);
        double df = dfNumerador / dfDenominador;

        // Aproximação do p-valor
        double pValor = 2 * (1 - cumulativeTDistribution(Math.abs(t), df));

        return new double[]{t, pValor, df};
    }

    /**
     * Função de distribuição acumulada da distribuição t de Student (aproximada)
     */
    private static double cumulativeTDistribution(double t, double df) {
        if (t <= 0) return 0.5;
        if (df <= 0) return 0;
        // Aproximação simplificada
        return 1 - (1 / (1 + Math.pow(t, 2) / df));
    }

    public static void garantirDiretorio(String filepath) {
        File file = new File(filepath);
        File parent = file.getParentFile();
        if (parent != null && !parent.exists()) {
            parent.mkdirs();
        }
    }

    public static void exportarCSV(String filename, Map<String, List<Long>> resultados) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }
        if (resultados == null || resultados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum resultado para exportar");
        }

        garantirDiretorio(filename);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            writer.print("Execucao");
            for (String algoritmo : resultados.keySet()) {
                writer.print("," + algoritmo);
            }
            writer.println();

            int maxExecucoes = resultados.values().stream()
                    .mapToInt(List::size)
                    .max()
                    .orElse(0);

            for (int i = 0; i < maxExecucoes; i++) {
                writer.print(i + 1);
                for (List<Long> tempos : resultados.values()) {
                    if (i < tempos.size()) {
                        writer.print("," + tempos.get(i));
                    } else {
                        writer.print(",");
                    }
                }
                writer.println();
            }
            System.out.println("  ✅ Arquivo salvo: " + filename);
        }
    }

    public static void imprimirComparacao(Map<String, List<Long>> resultados) {
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
            ResultadoEstatistico est = calcularEstatisticas(entry.getValue());
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

    /**
     * Calcula o coeficiente de variação
     */
    public static double coeficienteVariacao(List<? extends Number> valores) {
        ResultadoEstatistico est = calcularEstatisticas(valores);
        if (est.media == 0) return Double.NaN;
        return est.desvioPadrao / Math.abs(est.media);
    }

    /**
     * Calcula a correlação de Pearson
     */
    public static double correlacaoPearson(List<? extends Number> x, List<? extends Number> y) {
        if (x == null || y == null || x.size() != y.size() || x.isEmpty()) {
            return Double.NaN;
        }

        ResultadoEstatistico estX = calcularEstatisticas(x);
        ResultadoEstatistico estY = calcularEstatisticas(y);

        double covariancia = 0;
        for (int i = 0; i < x.size(); i++) {
            double dx = x.get(i).doubleValue() - estX.media;
            double dy = y.get(i).doubleValue() - estY.media;
            covariancia += dx * dy;
        }
        covariancia /= x.size();

        double produtoDesvios = estX.desvioPadrao * estY.desvioPadrao;
        if (produtoDesvios == 0) return Double.NaN;

        return covariancia / produtoDesvios;
    }

    /**
     * Remove outliers usando o método IQR
     */
    public static List<Double> removerOutliers(List<? extends Number> valores) {
        if (valores == null || valores.isEmpty()) {
            return new ArrayList<>();
        }

        List<Double> dados = new ArrayList<>();
        for (Number n : valores) {
            dados.add(n.doubleValue());
        }

        Collections.sort(dados);

        int q1Index = dados.size() / 4;
        int q3Index = 3 * dados.size() / 4;

        double q1 = dados.get(q1Index);
        double q3 = dados.get(q3Index);
        double iqr = q3 - q1;

        double limiteInferior = q1 - 1.5 * iqr;
        double limiteSuperior = q3 + 1.5 * iqr;

        List<Double> semOutliers = new ArrayList<>();
        for (double d : dados) {
            if (d >= limiteInferior && d <= limiteSuperior) {
                semOutliers.add(d);
            }
        }

        return semOutliers;
    }

    /**
     * Calcula resumo estatístico rápido
     */
    public static String resumoRapido(List<? extends Number> valores) {
        ResultadoEstatistico est = calcularEstatisticas(valores);
        return String.format(Locale.US,
                "n=%d, μ=%.2f, σ=%.2f, min=%.2f, max=%.2f",
                est.tamanhoAmostra, est.media, est.desvioPadrao, est.minimo, est.maximo
        );
    }

    /**
     * Versão segura de exportarCSV que não lança exceção
     */
    public static boolean exportarCSVSafe(String filename, Map<String, List<Long>> resultados) {
        try {
            exportarCSV(filename, resultados);
            return true;
        } catch (IOException e) {
            System.err.println("Falha ao exportar para " + filename + ": " + e.getMessage());
            return false;
        }
    }
}