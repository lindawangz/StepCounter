package Plot;

import javax.swing.*;

public class PlotWindow {
    private final JFrame frame;

    private PlotWindow(ScatterPlot plot, int width, int height) {
        frame = new JFrame("Step Counter Plot");
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setSize(width, height);
        frame.setLocationRelativeTo(null);
        frame.setContentPane(plot);
    }

    public static PlotWindow getWindowFor(ScatterPlot plot, int width, int height) {
        return new PlotWindow(plot, width, height);
    }

    public void show() {
        SwingUtilities.invokeLater(() -> frame.setVisible(true));
    }
}
