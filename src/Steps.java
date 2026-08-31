import Plot.PlotWindow;
import Plot.ScatterPlot;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;

public class Steps {
    public static void main(String[] args) throws IOException {
        String data = readFile("1-200-step-regular.csv");
        String[] lines = data.split("\n");

        ArrayList<Double> accX = getColumnAsList(lines, 0);
        ArrayList<Double> accY = getColumnAsList(lines, 1);
        ArrayList<Double> accZ = getColumnAsList(lines, 2);

        ArrayList<Double> mags = getMagnitudes(accX, accY, accZ);
        ArrayList<Integer> peakIndexes = getPeakIndexes(mags);
        ArrayList<Double> peakValues = getValuesAt(peakIndexes, mags);

        System.out.println("Steps: " + getSteps(mags));

        ScatterPlot plt = new ScatterPlot(100, 100, 1100, 700);

        for (int i = 0; i < mags.size(); i++) {
            plt.plot(0, i, mags.get(i))
                    .strokeColor("red")
                    .strokeWeight(2)
                    .style("-");
        }

        for (int i = 0; i < peakIndexes.size(); i++) {
            plt.plot(1, peakIndexes.get(i), peakValues.get(i))
                    .strokeColor("blue")
                    .strokeWeight(5)
                    .style(".");
        }

        PlotWindow window = PlotWindow.getWindowFor(plt, 1200, 800);
        window.show();
    }

    public static ArrayList<Integer> getPeakIndexes(ArrayList<Double> mag) {
        ArrayList<Integer> peakLocations = new ArrayList<>();
        for (int i = 1; i < mag.size() - 1; i++) {
            if (ifPeak(mag, i)) {
                peakLocations.add(i);
            }
        }
        return peakLocations;
    }

    public static ArrayList<Double> getValuesAt(ArrayList<Integer> peakLocations,
                                                 ArrayList<Double> values) {
        ArrayList<Double> yValues = new ArrayList<>();
        for (int i = 0; i < peakLocations.size(); i++) {
            yValues.add(values.get(peakLocations.get(i)));
        }
        return yValues;
    }

    public static ArrayList<Double> getMagnitudes(ArrayList<Double> x,
                                                   ArrayList<Double> y,
                                                   ArrayList<Double> z) {
        ArrayList<Double> magnitudes = new ArrayList<>();
        for (int i = 0; i < x.size(); i++) {
            double squareX = x.get(i) * x.get(i);
            double squareY = y.get(i) * y.get(i);
            double squareZ = z.get(i) * z.get(i);
            magnitudes.add(Math.sqrt(squareX + squareY + squareZ));
        }
        return magnitudes;
    }

    public static int getSteps(ArrayList<Double> magnitudes) {
        int steps = 0;
        for (int i = 1; i < magnitudes.size() - 1; i++) {
            if (ifPeak(magnitudes, i)) {
                steps++;
            }
        }
        return steps;
    }

    public static boolean ifPeak(ArrayList<Double> magnitudes, int i) {
        return magnitudes.get(i - 1) < magnitudes.get(i)
                && magnitudes.get(i) > magnitudes.get(i + 1);
    }

    public static ArrayList<Double> getColumnAsList(String[] lines, int columnNumber) {
        ArrayList<Double> colVals = new ArrayList<>();
        for (int i = 1; i < lines.length; i++) {
            if (lines[i].trim().isEmpty()) continue;
            String[] vals = lines[i].split(",");
            colVals.add(Double.parseDouble(vals[columnNumber].trim()));
        }
        return colVals;
    }

    private static String readFile(String filePath) {
        StringBuilder sb = new StringBuilder();

        try (BufferedReader br = Files.newBufferedReader(Paths.get(filePath))) {
            String line = br.readLine();

            while (line != null) {
                sb.append(line).append(System.lineSeparator());
                line = br.readLine();
            }
        } catch (Exception errorObj) {
            System.err.println("Couldn't read file: " + filePath);
            errorObj.printStackTrace();
        }

        return sb.toString();
    }
}
