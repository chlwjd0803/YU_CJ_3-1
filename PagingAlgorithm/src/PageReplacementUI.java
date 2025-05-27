import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.util.Random;

public class PageReplacementUI extends JFrame {
    private JTextField tfCapacity, tfLength, tfRange;
    private JSpinner spRuns;
    private JButton btnRun;
    private JTable table;
    private DefaultTableModel tableModel;

    public PageReplacementUI() {
        super("Page Replacement Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input panel
        JPanel inputPanel = new JPanel(new GridLayout(4, 2, 5, 5));
        inputPanel.setBorder(BorderFactory.createTitledBorder("시뮬레이션 파라미터"));
        inputPanel.add(new JLabel("프레임 크기:"));
        tfCapacity = new JTextField("2");
        inputPanel.add(tfCapacity);
        inputPanel.add(new JLabel("Reference String 길이:"));
        tfLength = new JTextField("100");
        inputPanel.add(tfLength);
        inputPanel.add(new JLabel("페이지 범위 (1~n):"));
        tfRange = new JTextField("10");
        inputPanel.add(tfRange);
        inputPanel.add(new JLabel("시도 횟수:"));
        spRuns = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1));
        inputPanel.add(spRuns);
        add(inputPanel, BorderLayout.NORTH);

        // Table for results
        String[] columns = {"시도", "알고리즘", "Page Fault", "Fault Rate (%)", "EAT (ms)"};
        tableModel = new DefaultTableModel(columns, 0) {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
        table = new JTable(tableModel);
        table.setAutoCreateRowSorter(true);
        add(new JScrollPane(table), BorderLayout.CENTER);

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
        // clear previous results
        tableModel.setRowCount(0);
        int capacity = Integer.parseInt(tfCapacity.getText());
        int length = Integer.parseInt(tfLength.getText());
        int range = Integer.parseInt(tfRange.getText());
        int runs = (Integer) spRuns.getValue();

        for (int i = 1; i <= runs; i++) {
            int[] refStr = generateRandomSequence(length, range);
            // Execute algorithms and get results
            PageFaultResult fifo = FIFO.algorithm(capacity, refStr);
            PageFaultResult lru  = LRU.algorithm(capacity, refStr);
            PageFaultResult opt  = Optimal.algorithm(capacity, refStr);

            // add rows for each algorithm
            tableModel.addRow(new Object[]{i, "FIFO", fifo.faults,
                    String.format("%.2f", fifo.rate * 100), String.format("%.4f", fifo.eat)});
            tableModel.addRow(new Object[]{i, "LRU", lru.faults,
                    String.format("%.2f", lru.rate * 100), String.format("%.4f", lru.eat)});
            tableModel.addRow(new Object[]{i, "OPT", opt.faults,
                    String.format("%.2f", opt.rate * 100), String.format("%.4f", opt.eat)});
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

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PageReplacementUI::new);
    }
}