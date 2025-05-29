import javax.swing.*;
import java.awt.*;

public class PieChartPanel extends JPanel {
    private int hits;
    private int faults;

    public PieChartPanel() {
        setPreferredSize(new Dimension(200, 200));
    }

    public void setData(int hits, int faults) {
        this.hits = hits;
        this.faults = faults;
        repaint();
    }

    @Override
    protected void paintComponent(Graphics g) {
        super.paintComponent(g);
        int total = hits + faults;
        if (total == 0) return;

        Graphics2D g2 = (Graphics2D) g;
        int diameter = Math.min(getWidth(), getHeight()) - 20;
        int x = (getWidth() - diameter) / 2;
        int y = (getHeight() - diameter) / 2;

        int hitAngle = (int) Math.round(360.0 * hits / total);
        g2.setColor(Color.GREEN);
        g2.fillArc(x, y, diameter, diameter, 0, hitAngle);
        g2.setColor(Color.RED);
        g2.fillArc(x, y, diameter, diameter, hitAngle, 360 - hitAngle);

        g2.setColor(Color.BLACK);
        g2.drawString("Hits: " + hits, 10, getHeight() - 30);
        g2.drawString("Faults: " + faults, 10, getHeight() - 15);
    }
}