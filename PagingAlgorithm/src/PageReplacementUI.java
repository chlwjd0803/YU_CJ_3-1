import javax.swing.*;
import java.awt.*;
import java.awt.event.*;
import java.util.Random;

public class PageReplacementUI extends JFrame {
    private JTextField tfCapacity, tfLength, tfRange;
    private JSpinner spRuns;
    private JButton btnRun;
    private JTextArea taResults;

    public PageReplacementUI() {
        super("Page Replacement Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("Simulation Parameters"));
        inputPanel.add(new JLabel("Frame Capacity:"));
        tfCapacity = new JTextField("2");
        inputPanel.add(tfCapacity);
        inputPanel.add(new JLabel("Reference String Length:"));
        tfLength = new JTextField("100");
        inputPanel.add(tfLength);
        inputPanel.add(new JLabel("Page Range (1~n):"));
        tfRange = new JTextField("10");
        inputPanel.add(tfRange);
        inputPanel.add(new JLabel("Number of Runs:"));
        spRuns = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(spRuns);
        add(inputPanel, BorderLayout.NORTH);

        // Results area
        taResults = new JTextArea(15, 50);
        taResults.setEditable(false);
        taResults.setFont(new Font(Font.MONOSPACED, Font.PLAIN, 12));
        add(new JScrollPane(taResults), BorderLayout.CENTER);

        // Run button
        btnRun = new JButton("Run Simulation");
        btnRun.addActionListener(e -> runSimulation());
        JPanel south = new JPanel();
        south.add(btnRun);
        add(south, BorderLayout.SOUTH);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    private void runSimulation() {
        taResults.setText("");
        int capacity = Integer.parseInt(tfCapacity.getText());
        int length = Integer.parseInt(tfLength.getText());
        int range = Integer.parseInt(tfRange.getText());
        int runs = (Integer) spRuns.getValue();

        for (int i = 1; i <= runs; i++) {
            int[] refStr = generateRandomSequence(length, range);
            // Call refactored algorithm classes
            PageFaultResult fifo = FIFO.algorithm(capacity, refStr);
            PageFaultResult lru  = LRU.algorithm(capacity, refStr);
            PageFaultResult opt  = Optimal.algorithm(capacity, refStr);

            taResults.append(String.format("Run %d:\n", i));
            taResults.append(String.format("FIFO: faults=%d, rate=%.2f%%, EAT=%.4f ms\n",
                    fifo.faults, fifo.rate * 100, fifo.eat));
            taResults.append(String.format("LRU : faults=%d, rate=%.2f%%, EAT=%.4f ms\n",
                    lru.faults,  lru.rate  * 100, lru.eat));
            taResults.append(String.format("OPT : faults=%d, rate=%.2f%%, EAT=%.4f ms\n\n",
                    opt.faults,  opt.rate  * 100, opt.eat));
        }
    }

    private int[] generateRandomSequence(int length, int range) {
        int[] seq = new int[length];
        Random rand = new Random();
        for (int i = 0; i < length; i++) {
            seq[i] = rand.nextInt(range) + 1;
        }
        return seq;
    }


}