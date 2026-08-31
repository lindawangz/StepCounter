package Plot;

import javax.swing.*;
import java.awt.*;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class ScatterPlot extends JPanel {

    public static class Point {
        final int dataSet;
        final double x;
        final double y;
        String color = "black";
        int weight = 2;
        String style = ".";

        Point(int dataSet, double x, double y) {
            this.dataSet = dataSet;
            this.x = x;
            this.y = y;
        }

        public Point strokeColor(String color) {
            this.color = color;
            return this;
        }

        public Point strokeWeight(int weight) {
            this.weight = weight;
            return this;
        }

        public Point style(String style) {
            this.style = style;
            return this;
        }
    }

    private final List<Point> points = new ArrayList<>();
    private final int plotLeft;
    private final int plotTop;
    private final int plotRight;
    private final int plotBottom;

    public ScatterPlot(int x1, int y1, int x2, int y2) {
        this.plotLeft = x1;
        this.plotTop = y1;
        this.plotRight = x2;
        this.plotBottom = y2;
        setBackground(Color.WHITE);
    }

    public Point plot(int dataSet, double x, double y) {
        Point p = new Point(dataSet, x, y);
        points.add(p);
        repaint();
        return p;
    }

    private Color parseColor(String name) {
        if (name == null) return Color.BLACK;
        switch (name.toLowerCase()) {
            case "red": return Color.RED;
            case "blue": return Color.BLUE;
            case "green": return Color.GREEN;
            case "gray":
            case "grey": return Color.GRAY;
            case "orange": return Color.ORANGE;
            case "magenta": return Color.MAGENTA;
            case "cyan": return Color.CYAN;
            case "yellow": return Color.YELLOW;
            default: return Color.BLACK;
        }
    }

    @Override
    protected void paintComponent(Graphics graphics) {
        super.paintComponent(graphics);
        if (points.isEmpty()) return;

        Graphics2D g = (Graphics2D) graphics.create();
        g.setRenderingHint(RenderingHints.KEY_ANTIALIASING,
                RenderingHints.VALUE_ANTIALIAS_ON);

        int left = Math.max(60, Math.min(plotLeft, getWidth() - 100));
        int top = Math.max(30, Math.min(plotTop, getHeight() - 100));
        int right = Math.min(getWidth() - 40, Math.max(left + 100, plotRight));
        int bottom = Math.min(getHeight() - 60, Math.max(top + 100, plotBottom));

        double minX = points.stream().mapToDouble(p -> p.x).min().orElse(0);
        double maxX = points.stream().mapToDouble(p -> p.x).max().orElse(1);
        double minY = points.stream().mapToDouble(p -> p.y).min().orElse(0);
        double maxY = points.stream().mapToDouble(p -> p.y).max().orElse(1);

        if (maxX == minX) maxX = minX + 1;
        if (maxY == minY) maxY = minY + 1;

        // Axes
        g.setColor(Color.DARK_GRAY);
        g.setStroke(new BasicStroke(1));
        g.drawLine(left, bottom, right, bottom);
        g.drawLine(left, top, left, bottom);

        // Basic labels
        g.drawString(String.format("%.2f", minY), 8, bottom);
        g.drawString(String.format("%.2f", maxY), 8, top + 5);
        g.drawString(String.format("%.0f", minX), left, bottom + 25);
        g.drawString(String.format("%.0f", maxX), right - 30, bottom + 25);

        Map<Integer, List<Point>> sets = new HashMap<>();
        for (Point p : points) {
            sets.computeIfAbsent(p.dataSet, k -> new ArrayList<>()).add(p);
        }

        for (List<Point> set : sets.values()) {
            set.sort(Comparator.comparingDouble(p -> p.x));

            Point previous = null;
            for (Point p : set) {
                int px = left + (int) ((p.x - minX) / (maxX - minX) * (right - left));
                int py = bottom - (int) ((p.y - minY) / (maxY - minY) * (bottom - top));

                g.setColor(parseColor(p.color));
                g.setStroke(new BasicStroke(Math.max(1, p.weight)));

                if ("-".equals(p.style)) {
                    if (previous != null) {
                        int prevX = left + (int) ((previous.x - minX) / (maxX - minX) * (right - left));
                        int prevY = bottom - (int) ((previous.y - minY) / (maxY - minY) * (bottom - top));
                        g.drawLine(prevX, prevY, px, py);
                    }
                } else {
                    int diameter = Math.max(3, p.weight + 2);
                    g.fillOval(px - diameter / 2, py - diameter / 2, diameter, diameter);
                }

                previous = p;
            }
        }

        g.dispose();
    }
}
