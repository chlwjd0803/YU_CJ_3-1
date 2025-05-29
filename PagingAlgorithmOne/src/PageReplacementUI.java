import javax.swing.*;
import java.awt.*;
import java.awt.event.*;

public class PageReplacementUI extends JFrame implements ActionListener {
    private JTextField refField;
    private JTextField frameField;
    private JComboBox<String> algorithmCombo;
    private JButton simulateButton;
    private JTextArea resultArea;

    public PageReplacementUI() {
        super("Page Replacement Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout());

        JPanel controlPanel = new JPanel(new GridLayout(0, 2, 5, 5));

        // 참조열 입력
        controlPanel.add(new JLabel("Reference String (comma-separated):"));
        refField = new JTextField();
        controlPanel.add(refField);

        // 프레임 수 입력
        controlPanel.add(new JLabel("Frame Capacity:"));
        frameField = new JTextField();
        controlPanel.add(frameField);

        // 알고리즘 선택
        controlPanel.add(new JLabel("Algorithm:"));
        algorithmCombo = new JComboBox<>(new String[]{"FIFO", "LRU", "Optimal"});
        controlPanel.add(algorithmCombo);

        // 시뮬레이션 실행 버튼
        simulateButton = new JButton("Simulate");
        simulateButton.addActionListener(this);
        controlPanel.add(simulateButton);
        controlPanel.add(new JLabel());  // placeholder

        add(controlPanel, BorderLayout.NORTH);

        // 결과 출력 영역
        resultArea = new JTextArea(15, 50);
        resultArea.setEditable(false);
        add(new JScrollPane(resultArea), BorderLayout.CENTER);

        pack();
        setLocationRelativeTo(null);
        setVisible(true);
    }

    @Override
    public void actionPerformed(ActionEvent e) {
        resultArea.setText("");
        String refText = refField.getText().trim();
        String algo = (String) algorithmCombo.getSelectedItem();
        if (refText.isEmpty() || frameField.getText().trim().isEmpty()) {
            JOptionPane.showMessageDialog(this, "Please enter reference string and frame capacity.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        String[] refStr = refText.split("\\s*,\\s*");
        int capacity;
        try {
            capacity = Integer.parseInt(frameField.getText().trim());
        } catch (NumberFormatException ex) {
            JOptionPane.showMessageDialog(this, "Frame capacity must be an integer.",
                    "Input Error", JOptionPane.ERROR_MESSAGE);
            return;
        }

        // 선택된 알고리즘에 따라 시뮬레이션 실행
        PageFaultResult.runWithStates(refStr, capacity, algo, resultArea);
    }

    public static void main(String[] args) {
        SwingUtilities.invokeLater(PageReplacementUI::new);
    }
}
