package SumoAutAv;

import au.com.bytecode.opencsv.CSVWriter;

import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class ExcelGenerator {

    public static void gerarExcel(ArrayList<double[]> tempos, ArrayList<double[]> velocidades) {
        if (tempos.size() != velocidades.size()) {
            System.out.println("Os ArrayLists de tempos e velocidades devem ter o mesmo tamanho.");
            return;
        }

        int numFluxos = tempos.get(0).length;

        try (CSVWriter writer = new CSVWriter(new FileWriter("ReconciliationData.csv"), ';')) {
            // Cabeçalho
            createHeader(writer, numFluxos);

            // Dados
            int numMedicoes = tempos.size();
            for (int i = 0; i < numMedicoes; i++) {
                createRow(writer, tempos, velocidades, i, numFluxos);
            }

            System.out.println("Arquivo CSV gerado com sucesso!");
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static void createHeader(CSVWriter writer, int numFluxos) {
        List<String> header = new ArrayList<>();
        for (int fluxo = 0; fluxo < numFluxos - 2; fluxo++) {
            header.add("F" + (fluxo + 1) + " Tempo (s)");
            header.add("F" + (fluxo + 1) + " Velocidade (m/s)");
        }
        writer.writeNext(header.toArray(new String[0]));
    }

    private static void createRow(CSVWriter writer, ArrayList<double[]> tempos, ArrayList<double[]> velocidades,
            int rowIndex, int numFluxos) {
        List<String> row = new ArrayList<>();

        for (int fluxo = 0; fluxo < numFluxos - 1; fluxo++) {
            row.add(String.valueOf(tempos.get(rowIndex)[fluxo]));
            row.add(String.valueOf(velocidades.get(rowIndex)[fluxo]));
        }

        writer.writeNext(row.toArray(new String[0]));
    }
}
