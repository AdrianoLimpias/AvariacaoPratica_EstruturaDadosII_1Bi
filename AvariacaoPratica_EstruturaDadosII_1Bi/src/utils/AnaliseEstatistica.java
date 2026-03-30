// src/utils/AnaliseEstatistica.java - VERSÃO CORRIGIDA COM IMPORTS COMPLETOS
package utils;

import java.util.*;
import java.io.IOException;
import java.io.FileWriter;
import java.io.PrintWriter;

/**
 * Classe utilitária para análise estatística dos experimentos
 */
public class AnaliseEstatistica {

    private static final double Z_95 = 1.96; // 95% de confiança
    private static final double Z_99 = 2.576; // 99% de confiança

    private AnaliseEstatistica() {}

    /**
     * Resultados da análise estatística
     */
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
        public double assimetria; // skewness
        public double curtose; // kurtosis
        public int tamanhoAmostra;

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

        /**
         * Retorna string formatada para CSV
         */
        public String toCSV() {
            return String.format(Locale.US,
                    "%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%.4f,%d",
                    media, mediana, desvioPadrao, variancia, minimo, maximo, amplitude,
                    assimetria, curtose, intervaloConfianca95, intervaloConfianca99,
                    tamanhoAmostra
            );
        }

        /**
         * Retorna array com os principais valores para fácil acesso
         */
        public double[] toArray() {
            return new double[]{
                    media, mediana, desvioPadrao, variancia, minimo, maximo, amplitude,
                    assimetria, curtose, intervaloConfianca95, intervaloConfianca99
            };
        }
    }

    /**
     * Calcula estatísticas básicas com validação
     */
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

        // Média
        double soma = 0;
        for (double d : dados) {
            soma += d;
        }
        resultado.media = soma / dados.size();

        // Mediana
        if (dados.size() % 2 == 0) {
            resultado.mediana = (dados.get(dados.size() / 2 - 1) + dados.get(dados.size() / 2)) / 2;
        } else {
            resultado.mediana = dados.get(dados.size() / 2);
        }

        // Variância e desvio padrão
        double somaQuadrados = 0;
        for (double d : dados) {
            somaQuadrados += Math.pow(d - resultado.media, 2);
        }
        resultado.variancia = somaQuadrados / dados.size();
        resultado.desvioPadrao = Math.sqrt(resultado.variancia);

        // Assimetria (skewness)
        if (resultado.desvioPadrao > 0) {
            double somaCubo = 0;
            for (double d : dados) {
                somaCubo += Math.pow(d - resultado.media, 3);
            }
            resultado.assimetria = somaCubo / (dados.size() * Math.pow(resultado.desvioPadrao, 3));
        } else {
            resultado.assimetria = 0;
        }

        // Curtose (kurtosis) - excesso
        if (resultado.desvioPadrao > 0) {
            double somaQuarta = 0;
            for (double d : dados) {
                somaQuarta += Math.pow(d - resultado.media, 4);
            }
            resultado.curtose = somaQuarta / (dados.size() * Math.pow(resultado.desvioPadrao, 4)) - 3;
        } else {
            resultado.curtose = 0;
        }

        // Intervalos de confiança
        double erroPadrao = resultado.desvioPadrao / Math.sqrt(dados.size());
        resultado.intervaloConfianca95 = Z_95 * erroPadrao;
        resultado.intervaloConfianca99 = Z_99 * erroPadrao;

        return resultado;
    }

    /**
     * Calcula estatísticas para arrays de double
     */
    public static ResultadoEstatistico calcularEstatisticas(double[] valores) {
        if (valores == null) return new ResultadoEstatistico();
        List<Double> lista = new ArrayList<>();
        for (double v : valores) {
            lista.add(v);
        }
        return calcularEstatisticas(lista);
    }

    /**
     * Calcula estatísticas para arrays de long
     */
    public static ResultadoEstatistico calcularEstatisticas(long[] valores) {
        if (valores == null) return new ResultadoEstatistico();
        List<Long> lista = new ArrayList<>();
        for (long v : valores) {
            lista.add(v);
        }
        return calcularEstatisticas(lista);
    }

    /**
     * Calcula estatísticas para arrays de int
     */
    public static ResultadoEstatistico calcularEstatisticas(int[] valores) {
        if (valores == null) return new ResultadoEstatistico();
        List<Integer> lista = new ArrayList<>();
        for (int v : valores) {
            lista.add(v);
        }
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

        // Aproximação usando função beta incompleta
        double x = (t + Math.sqrt(t * t + df)) / (2 * Math.sqrt(t * t + df));
        return 1 - incompleteBeta(df / 2, df / 2, x);
    }

    /**
     * Função beta incompleta regularizada (aproximação)
     */
    private static double incompleteBeta(double a, double b, double x) {
        if (x < 0 || x > 1) return 0;
        if (x == 0) return 0;
        if (x == 1) return 1;

        // Aproximação simplificada para cálculos práticos
        // Em produção, usar biblioteca Apache Commons Math
        return Math.pow(x, a) * Math.pow(1 - x, b) / (a * beta(a, b));
    }

    private static double beta(double a, double b) {
        return Math.exp(logGamma(a) + logGamma(b) - logGamma(a + b));
    }

    private static double logGamma(double x) {
        // Aproximação de Lanczos para log gamma
        double[] coeff = {76.18009172947146, -86.50532032941677, 24.01409824083091,
                -1.231739572450155, 0.1208650973866179e-2, -0.5395239384953e-5};
        double y = x;
        double tmp = x + 5.5;
        tmp -= (x + 0.5) * Math.log(tmp);
        double ser = 1.000000000190015;
        for (int i = 0; i < 6; i++) {
            y += 1;
            ser += coeff[i] / y;
        }
        return -tmp + Math.log(2.5066282746310005 * ser / x);
    }

    /**
     * Calcula percentis dos dados
     */
    public static Map<Integer, Double> calcularPercentis(List<? extends Number> valores, int... percentis) {
        if (valores == null || valores.isEmpty()) {
            return new HashMap<>();
        }

        List<Double> dados = new ArrayList<>();
        for (Number n : valores) {
            dados.add(n.doubleValue());
        }
        Collections.sort(dados);

        Map<Integer, Double> resultados = new LinkedHashMap<>();

        for (int p : percentis) {
            if (p < 0 || p > 100) continue;

            double posicao = (p / 100.0) * (dados.size() - 1);
            int indiceInferior = (int) Math.floor(posicao);
            int indiceSuperior = (int) Math.ceil(posicao);

            if (indiceInferior == indiceSuperior) {
                resultados.put(p, dados.get(indiceInferior));
            } else {
                double peso = posicao - indiceInferior;
                double valor = dados.get(indiceInferior) * (1 - peso) + dados.get(indiceSuperior) * peso;
                resultados.put(p, valor);
            }
        }

        return resultados;
    }

    /**
     * Calcula coeficiente de variação (CV = σ/μ)
     */
    public static double coeficienteVariacao(List<? extends Number> valores) {
        ResultadoEstatistico est = calcularEstatisticas(valores);
        if (est.media == 0) return Double.NaN;
        return est.desvioPadrao / Math.abs(est.media);
    }

    /**
     * Calcula correlação de Pearson entre duas amostras
     * @return valor da correlação (-1 a 1)
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
     * Teste de normalidade
     * @return true se os dados são normalmente distribuídos (alpha = 0.05)
     */
    public static boolean testeNormalidade(List<? extends Number> valores, double alpha) {
        if (valores == null || valores.size() < 3) return false;

        ResultadoEstatistico est = calcularEstatisticas(valores);

        // Critérios para normalidade
        boolean assimetriaNormal = Math.abs(est.assimetria) < 1.0;
        boolean curtoseNormal = Math.abs(est.curtose) < 2.0;

        // Teste de Jarque-Bera
        double jarqueBera = (valores.size() / 6.0) * (Math.pow(est.assimetria, 2) +
                Math.pow(est.curtose, 2) / 4.0);
        boolean jarqueBeraNormal = jarqueBera < 5.99; // Qui-quadrado com 2 gl, alpha=0.05

        return assimetriaNormal && curtoseNormal && jarqueBeraNormal;
    }

    /**
     * Remove outliers usando o método IQR
     * @return Nova lista sem outliers
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
     * Gera histograma formatado
     */
    public static void imprimirHistograma(List<? extends Number> valores, int numBins) {
        if (valores == null || valores.isEmpty() || numBins <= 0) {
            System.out.println("Dados insuficientes para histograma");
            return;
        }

        List<Double> dados = new ArrayList<>();
        for (Number n : valores) {
            dados.add(n.doubleValue());
        }

        double min = Collections.min(dados);
        double max = Collections.max(dados);
        double intervalo = (max - min) / numBins;

        if (intervalo == 0) {
            System.out.println("Todos os valores são iguais");
            return;
        }

        int[] bins = new int[numBins];

        for (double d : dados) {
            int bin = (int) ((d - min) / intervalo);
            bin = Math.min(bin, numBins - 1);
            bins[bin]++;
        }

        int maxFreq = Arrays.stream(bins).max().getAsInt();
        int escala = Math.max(1, maxFreq / 50);

        System.out.println("\nHistograma (cada █ = " + escala + " ocorrência(s)):");
        System.out.println("-".repeat(70));

        for (int i = 0; i < numBins; i++) {
            double inicio = min + i * intervalo;
            double fim = inicio + intervalo;

            StringBuilder barra = new StringBuilder();
            int numBarras = bins[i] / escala;
            for (int j = 0; j < numBarras; j++) {
                barra.append("█");
            }

            System.out.printf(Locale.US, "[%8.2f - %8.2f] %-30s %5d%n",
                    inicio, fim, barra.toString(), bins[i]);
        }
    }

    /**
     * Imprime comparação entre algoritmos com formatação melhorada
     */
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
     * Exporta resultados para CSV com cabeçalho
     */
    public static void exportarCSV(String filename, Map<String, List<Long>> resultados) throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }

        if (resultados == null || resultados.isEmpty()) {
            throw new IllegalArgumentException("Nenhum resultado para exportar");
        }

        // Garantir que o diretório existe
        garantirDiretorio(filename);

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Cabeçalho
            writer.print("Execucao");
            for (String algoritmo : resultados.keySet()) {
                writer.print("," + algoritmo);
            }
            writer.println();

            // Dados
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
        } catch (IOException e) {
            System.err.println("  ❌ Erro ao exportar dados: " + e.getMessage());
            throw e;
        }
    }

    /**
     * Exporta resultados estatísticos para CSV
     */
    public static void exportarEstatisticasCSV(String filename, Map<String, ResultadoEstatistico> resultados)
            throws IOException {
        if (filename == null || filename.trim().isEmpty()) {
            throw new IllegalArgumentException("Nome do arquivo inválido");
        }

        try (PrintWriter writer = new PrintWriter(new FileWriter(filename))) {
            // Cabeçalho
            writer.println("Algoritmo,Media,Mediana,DesvioPadrao,Minimo,Maximo,Amplitude,Assimetria,Curtose,IC95,IC99,N");

            // Dados
            for (Map.Entry<String, ResultadoEstatistico> entry : resultados.entrySet()) {
                ResultadoEstatistico est = entry.getValue();
                writer.printf(Locale.US, "%s,%f,%f,%f,%f,%f,%f,%f,%f,%f,%f,%d%n",
                        entry.getKey(),
                        est.media, est.mediana, est.desvioPadrao, est.minimo, est.maximo,
                        est.amplitude, est.assimetria, est.curtose,
                        est.intervaloConfianca95, est.intervaloConfianca99,
                        est.tamanhoAmostra);
            }

            System.out.println("Estatísticas exportadas com sucesso para: " + filename);
        }
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
     * Versão segura de exportarCSV que não lança exceção (usa try-catch interno)
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
// src/utils/AnaliseEstatistica.java - ADICIONAR ESTE MÉ

/**
 * Garante que o diretório existe antes de exportar
 */
public static void garantirDiretorio(String filepath) {
    File file = new File(filepath);
    File parent = file.getParentFile();
    if (parent != null && !parent.exists()) {
        boolean criado = parent.mkdirs();
        if (criado) {
            System.out.println("📁 Diretório criado: " + parent.getPath());
        }
    }
}