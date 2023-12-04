package SumoAutAv;

import java.util.ArrayList;

import javax.swing.SwingUtilities;

import org.apache.commons.math3.stat.descriptive.DescriptiveStatistics;

public class MathUtils {

    public static double calcularMedia(double[] valores) {
        DescriptiveStatistics stats = new DescriptiveStatistics(valores);
        return stats.getMean();
    }

    public static double calcularDesvioPadrao(double[] valores) {
        DescriptiveStatistics stats = new DescriptiveStatistics(valores);
        return stats.getStandardDeviation();
    }

    public static double calcularPolarizacao(double[] valores, double valorEsperado) {
        double media = calcularMedia(valores);
        return media - valorEsperado;
    }

    public static double calcularPrecisao(double[] valores) {
        return 2 * calcularDesvioPadrao(valores);
    }

    public static double calcularIncerteza(double[] valores) {
        return Math.sqrt(Math.pow(calcularPrecisao(valores), 2) + Math.pow(calcularMedia(valores), 2));
    }

    public static void calcularDadosEstatisticos(ArrayList<double[]> velocidadesReconciliadas,
            ArrayList<double[]> temposReconciliados, int indexFluxo,
            ArrayList<Double> estimativas) {
        double[] valores = new double[100];
        for (int i = 0; i < velocidadesReconciliadas.size(); i++) {
            valores[i] = velocidadesReconciliadas.get(i)[indexFluxo];
        }

        double media = calcularMedia(valores);
        double desvioPadrao = calcularDesvioPadrao(valores);
        double polarização = calcularPolarizacao(valores, estimativas.get(indexFluxo));
        double precisao = calcularPrecisao(valores);
        double incerteza = calcularIncerteza(valores);

        System.out.println("Fluxo " + (indexFluxo + 1));
        System.out.println("Média: " + media);
        System.out.println("Desvio Padrao: " + desvioPadrao);
        System.out.println("Polarizacao: " + polarização);
        System.out.println("Precisao: " + precisao);
        System.out.println("Incerteza: " + incerteza);

        ExcelGenerator.gerarExcel(velocidadesReconciliadas, temposReconciliados);
        SwingUtilities.invokeLater(() -> new GraphGenerator(temposReconciliados, velocidadesReconciliadas));
    }
}
