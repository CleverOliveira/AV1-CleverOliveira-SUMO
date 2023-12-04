package SumoAutAv;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.xy.XYSeries;
import org.jfree.data.xy.XYSeriesCollection;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;

public class GraphGenerator extends JFrame {
    public GraphGenerator(ArrayList<double[]> tempos, ArrayList<double[]> velocidades) {
        super("Velocidades Reconciliadas");

        XYSeriesCollection dataset = createDataset(velocidades);
        JFreeChart chart = ChartFactory.createXYLineChart(
                "Graph Generator", // Título do gráfico
                "Fluxo", // Rótulo do eixo x
                "Velocidade (m/s)", // Rótulo do eixo y
                dataset);

        XYPlot plot = chart.getXYPlot();
        plot.setDomainGridlinesVisible(true);
        plot.setRangeGridlinesVisible(true);

        XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer();
        renderer.setSeriesLinesVisible(0, true);
        renderer.setSeriesShapesVisible(0, true);
        // Adicione isso se quiser personalizar a forma dos pontos
        // renderer.setSeriesShape(0, new Ellipse2D.Double(-3, -3, 6, 6));

        plot.setRenderer(renderer);

        ChartPanel chartPanel = new ChartPanel(chart);
        chartPanel.setPreferredSize(new Dimension(800, 600));
        setContentPane(chartPanel);

        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private XYSeriesCollection createDataset(ArrayList<double[]> velocidades) {
        XYSeriesCollection dataset = new XYSeriesCollection();

        int numFluxos = velocidades.size();
        for (int fluxo = 0; fluxo < numFluxos; fluxo++) {
            XYSeries series = new XYSeries("Medição " + (fluxo + 1));

            int numMedicoes = velocidades.get(fluxo).length;
            for (int i = 0; i < numMedicoes; i++) {
                series.add(i, velocidades.get(fluxo)[i]);
            }

            dataset.addSeries(series);
        }

        return dataset;
    }
}
