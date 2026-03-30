// src/projeto1/caixeiro/CaixeiroViajante.java
package projeto1.caixeiro;

import java.util.*;

public class CaixeiroViajante {

    // Heurística do Vizinho Mais Próximo
    public static double vizinhoMaisProximo(double[][] distancia) {
        int n = distancia.length;
        boolean[] visitado = new boolean[n];
        List<Integer> caminho = new ArrayList<>();

        int atual = 0;
        visitado[atual] = true;
        caminho.add(atual);

        double custoTotal = 0;

        for (int i = 0; i < n - 1; i++) {
            int proximo = -1;
            double menorDistancia = Double.MAX_VALUE;

            for (int j = 0; j < n; j++) {
                if (!visitado[j] && distancia[atual][j] < menorDistancia) {
                    menorDistancia = distancia[atual][j];
                    proximo = j;
                }
            }

            if (proximo != -1) {
                custoTotal += menorDistancia;
                visitado[proximo] = true;
                caminho.add(proximo);
                atual = proximo;
            }
        }

        // Retornar à cidade inicial
        custoTotal += distancia[atual][0];

        return custoTotal;
    }

    // Heurística de Inserção Mais Próxima
    public static double insercaoMaisProxima(double[][] distancia) {
        int n = distancia.length;
        List<Integer> caminho = new ArrayList<>();
        boolean[] noCaminho = new boolean[n];

        // Começar com a cidade 0
        caminho.add(0);
        noCaminho[0] = true;

        // Encontrar a cidade mais próxima da cidade 0 para iniciar
        int proximaCidade = -1;
        double menorDist = Double.MAX_VALUE;
        for (int i = 1; i < n; i++) {
            if (distancia[0][i] < menorDist) {
                menorDist = distancia[0][i];
                proximaCidade = i;
            }
        }

        caminho.add(proximaCidade);
        noCaminho[proximaCidade] = true;

        // Inserir as demais cidades
        for (int k = 2; k < n; k++) {
            // Encontrar cidade não visitada com menor distância para qualquer cidade no caminho
            int cidadeParaInserir = -1;
            double menorDistanciaParaCaminho = Double.MAX_VALUE;

            for (int i = 0; i < n; i++) {
                if (!noCaminho[i]) {
                    for (int j = 0; j < caminho.size(); j++) {
                        int cidadeCaminho = caminho.get(j);
                        if (distancia[i][cidadeCaminho] < menorDistanciaParaCaminho) {
                            menorDistanciaParaCaminho = distancia[i][cidadeCaminho];
                            cidadeParaInserir = i;
                        }
                    }
                }
            }

            // Encontrar melhor posição para inserir
            int melhorPosicao = -1;
            double menorCustoIncremento = Double.MAX_VALUE;

            for (int i = 0; i < caminho.size(); i++) {
                int cidade1 = caminho.get(i);
                int cidade2 = caminho.get((i + 1) % caminho.size());

                double incremento = distancia[cidade1][cidadeParaInserir] +
                        distancia[cidadeParaInserir][cidade2] -
                        distancia[cidade1][cidade2];

                if (incremento < menorCustoIncremento) {
                    menorCustoIncremento = incremento;
                    melhorPosicao = i + 1;
                }
            }

            caminho.add(melhorPosicao, cidadeParaInserir);
            noCaminho[cidadeParaInserir] = true;
        }

        // Calcular custo total
        double custoTotal = 0;
        for (int i = 0; i < caminho.size() - 1; i++) {
            custoTotal += distancia[caminho.get(i)][caminho.get(i + 1)];
        }
        custoTotal += distancia[caminho.get(caminho.size() - 1)][caminho.get(0)];

        return custoTotal;
    }

    // Gerar matriz de distâncias aleatória
    public static double[][] gerarDistancias(int n) {
        double[][] distancias = new double[n][n];
        Random rand = new Random();

        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (i == j) {
                    distancias[i][j] = 0;
                } else {
                    distancias[i][j] = 10 + rand.nextDouble() * 90; // Distâncias entre 10 e 100
                    distancias[j][i] = distancias[i][j]; // Matriz simétrica
                }
            }
        }

        return distancias;
    }
}