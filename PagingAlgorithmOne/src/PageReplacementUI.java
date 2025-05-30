import javax.swing.*;
import javax.swing.border.LineBorder;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

public class PageReplacementUI extends JFrame implements ActionListener {
    private JTextField refField;
    private JTextField frameField;
    private JComboBox<String> algorithmCombo;
    private JButton simulateButton;
    private JPanel visualPanel;
    private PieChartPanel chartPanel;

    public PageReplacementUI() {
        super("페이지 교체정책 시뮬레이터");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel controlPanel = new JPanel(new FlowLayout(FlowLayout.LEFT, 10, 10));
        controlPanel.add(new JLabel("Reference String:"));
        refField = new JTextField(20);
        controlPanel.add(refField);
        controlPanel.add(new JLabel("프레임 개수:"));
        frameField = new JTextField(3);
        controlPanel.add(frameField);
        controlPanel.add(new JLabel("알고리즘:"));
        algorithmCombo = new JComboBox<>(new String[]{"FIFO", "LRU", "Optimal", "OptiLRU"});
        controlPanel.add(algorithmCombo);
        simulateButton = new JButton("실행");
        simulateButton.addActionListener(this);
        controlPanel.add(simulateButton);
        add(controlPanel, BorderLayout.NORTH);

        visualPanel = new JPanel();
        visualPanel.setLayout(new BoxLayout(visualPanel, BoxLayout.Y_AXIS));
        add(new JScrollPane(visualPanel), BorderLayout.CENTER);

        chartPanel = new PieChartPanel();
        add(chartPanel, BorderLayout.EAST);

        setSize(1000, 600);
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        visualPanel.removeAll();
        String refText = refField.getText().trim();
        String algo = (String) algorithmCombo.getSelectedItem();
        if (refText.isEmpty() || frameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "모두 입력을 완료해주세요.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int[] refs;
        try {
            refs = Arrays.stream(refText.split("\\s*,\\s*")).mapToInt(Integer::parseInt).toArray();
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "콤마 구분해서 입력하셔야 합니다",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }
        int capacity;
        try {
            capacity = Integer.parseInt(frameField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "프레임 개수는 정수여야 합니다.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 요약 계산
        PageFaultResult summary = switch (algo) {
            case "FIFO" -> FIFO.algorithm(capacity, refs);
            case "LRU" -> LRU.algorithm(capacity, refs);
            case "Optimal" -> Optimal.algorithm(capacity, refs);
            case "OptiLRU" -> OptiLRU.algorithm(capacity, refs);
            default -> throw new IllegalArgumentException("알고리즘 선택이 잘못됨");
        };
        int faults = summary.faults;
        int hits = refs.length - faults;
        chartPanel.setData(hits, faults);

        // 시각적 단계별 표시
        List<Integer> frame = new ArrayList<>();
        Deque<Integer> recent = new LinkedList<>();
        Set<Integer> exist = new HashSet<>();

        for (int i = 0; i < refs.length; i++) {
            int page = refs[i];
            boolean isHit = exist.contains(page);
            Integer evicted = null;

            if (!isHit) {
                if (frame.size() >= capacity) {
                    if ("FIFO".equals(algo)) {
                        evicted = frame.remove(0);
                    } else if ("LRU".equals(algo)) {
                        evicted = recent.pollFirst();
                        frame.remove(evicted);
                    } else { // Optimal
                        int farthest = -1, ev = frame.get(0);
                        for (int p : frame) {
                            int next = refs.length;
                            for (int j = i + 1; j < refs.length; j++) {
                                if (refs[j] == p) {
                                    next = j;
                                    break;
                                }
                            }
                            if (next > farthest) {
                                farthest = next;
                                ev = p;
                            }
                        }
                        evicted = ev;
                        frame.remove(evicted);
                    }
                    exist.remove(evicted);
                }
                frame.add(page);
                exist.add(page);
                if ("LRU".equals(algo)) {
                    recent.addLast(page);
                }
            } else if ("LRU".equals(algo)) {
                recent.remove(page);
                recent.addLast(page);
            }

            // Draw current step
            JPanel step = new JPanel(new FlowLayout(FlowLayout.LEFT, 5, 5));
            step.setBorder(BorderFactory.createTitledBorder("단계 " + (i + 1) + (isHit ? " (Hit)" : " (Fault)")));
            for (int slot = 0; slot < capacity; slot++) {
                JLabel cell = new JLabel();
                cell.setPreferredSize(new Dimension(40, 40));
                cell.setHorizontalAlignment(SwingConstants.CENTER);
                Color borderColor = (evicted != null && slot < frame.size() && frame.get(slot) == evicted)
                        ? Color.RED : Color.BLACK;
                int thickness = (evicted != null && slot < frame.size() && frame.get(slot) == evicted)
                        ? 2 : 1;
                cell.setBorder(new LineBorder(borderColor, thickness));
                if (slot < frame.size()) {
                    int val = frame.get(slot);
                    cell.setText(String.valueOf(val));
                    if (!isHit && val == page) {
                        cell.setBackground(Color.YELLOW);
                        cell.setOpaque(true);
                    }
                }
                step.add(cell);
            }
            visualPanel.add(step);
        }

        visualPanel.revalidate();
        visualPanel.repaint();
    }
}