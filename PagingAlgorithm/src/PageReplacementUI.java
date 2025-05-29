import javax.swing.*;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;
import java.util.List;

public class PageReplacementUI extends JFrame {
    private JTextField tfCapacity, tfLength, tfRange;
    private JSpinner spRuns;
    private JButton btnRun;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;

    private BarChartPanel faultBar;
    private BarChartPanel eatBar;
    private PieChartPanel faultPie;
    private StackedBarChartPanel stackedBar;

    public PageReplacementUI() {
        super("페이지 교체정책 시뮬레이터");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        JPanel input = new JPanel(new GridLayout(4, 2, 5, 5));
        input.setBorder(BorderFactory.createTitledBorder("파라미터"));
        input.add(new JLabel("프레임 크기 :")); tfCapacity = new JTextField("10"); input.add(tfCapacity);
        input.add(new JLabel("Ref String 길이 :")); tfLength = new JTextField("500"); input.add(tfLength);
        input.add(new JLabel("페이지 범위 (1~n) :")); tfRange = new JTextField("50"); input.add(tfRange);
        input.add(new JLabel("시행 횟수:")); spRuns = new JSpinner(new SpinnerNumberModel(1, 1, 100, 1)); input.add(spRuns);
        add(input, BorderLayout.NORTH);

        String[] cols = {"시행", "알고리즘", "Fault", "비율(%)", "EAT(ms)"};
        tableModel = new DefaultTableModel(cols, 0) { @Override public boolean isCellEditable(int r, int c) { return false; } };
        table = new JTable(tableModel);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable t, Object val,
                                                           boolean sel, boolean focus, int row, int col) {
                Component c = super.getTableCellRendererComponent(t, val, sel, focus, row, col);
                if (!sel) {
                    int group = row / 3;
                    c.setBackground((group % 2 == 0) ? Color.WHITE : new Color(240, 240, 255));
                }
                setHorizontalAlignment(col > 1 ? SwingConstants.CENTER : SwingConstants.LEFT);
                return c;
            }
        });
        table.setRowHeight(24);
        table.getTableHeader().setFont(new Font(Font.SANS_SERIF, Font.BOLD, 12));
        table.getColumn("비율(%)").setCellRenderer(new ProgressBarRenderer(0, 100));
        JScrollPane tblPane = new JScrollPane(table);

        faultBar = new BarChartPanel("평균 Page Faults");
        eatBar   = new BarChartPanel("평균 EAT (ms)");
        faultPie = new PieChartPanel("Fault 분포도");
        stackedBar = new StackedBarChartPanel("Hit vs Fault (%)");
        JPanel chartGrid = new JPanel(new GridLayout(2, 2, 5, 5));
        chartGrid.add(faultBar);
        chartGrid.add(faultPie);
        chartGrid.add(eatBar);
        chartGrid.add(stackedBar);

        tabbedPane = new JTabbedPane();
        tabbedPane.addTab("결과", tblPane);
        tabbedPane.addTab("차트", chartGrid);
        add(tabbedPane, BorderLayout.CENTER);

        btnRun = new JButton("시뮬레이션 실행");
        btnRun.addActionListener(e -> runSimulation());
        JPanel south = new JPanel(); south.add(btnRun);
        add(south, BorderLayout.SOUTH);

        pack(); setLocationRelativeTo(null); setVisible(true);
    }

    private void runSimulation() {
        tableModel.setRowCount(0);
        faultBar.setData(Collections.emptyMap());
        eatBar.setData(Collections.emptyMap());
        faultPie.setData(Collections.emptyMap());
        stackedBar.setData(Collections.emptyMap());

        int cap = Integer.parseInt(tfCapacity.getText());
        int len = Integer.parseInt(tfLength.getText());
        int range = Integer.parseInt(tfRange.getText());
        int runs = (Integer) spRuns.getValue();

        Map<String, List<Integer>> fm = new LinkedHashMap<>();
        Map<String, List<Double>> em = new LinkedHashMap<>();
        Map<String, List<Double>> rm = new LinkedHashMap<>();
        for (String a : Arrays.asList("FIFO","LRU","OPT","OPTLRU")) {
            fm.put(a, new ArrayList<>());
            em.put(a, new ArrayList<>());
            rm.put(a, new ArrayList<>());
        }

        for (int i = 1; i <= runs; i++) {
            int[] ref = generateRandomSequence(len, range);
            PageFaultResult r1 = FIFO.algorithm(cap, ref);
            PageFaultResult r2 = LRU.algorithm(cap, ref);
            PageFaultResult r3 = Optimal.algorithm(cap, ref);
            PageFaultResult r4 = OptiLRU.algorithm(cap, ref);
            PageFaultResult[] rs = {r1, r2, r3, r4};
            String[] names = {"FIFO", "LRU", "OPT", "OPTLRU"};
            for (int j = 0; j < 3; j++) {
                PageFaultResult r = rs[j];
                String nm = names[j];
                tableModel.addRow(new Object[]{
                        i, nm, r.faults,
                        r.rate * 100,
                        String.format("%.4f", r.eat)
                });
                fm.get(nm).add(r.faults);
                em.get(nm).add(r.eat);
                rm.get(nm).add(r.rate);
            }
        }
        Map<String, Double> avgF = new LinkedHashMap<>();
        Map<String, Double> avgE = new LinkedHashMap<>();
        Map<String, Double> avgR = new LinkedHashMap<>();
        for (String a : fm.keySet()) {
            avgF.put(a, fm.get(a).stream().mapToInt(x->x).average().orElse(0));
            avgE.put(a, em.get(a).stream().mapToDouble(x->x).average().orElse(0));
            avgR.put(a, rm.get(a).stream().mapToDouble(x->x).average().orElse(0) * 100);
        }
        faultBar.setData(avgF);
        eatBar.setData(avgE);
        faultPie.setData(avgF);
        stackedBar.setData(avgR);
        faultBar.repaint(); eatBar.repaint(); faultPie.repaint(); stackedBar.repaint();
    }

    private int[] generateRandomSequence(int l, int r) {
        Random rnd = new Random();
        int[] s = new int[l];
        for (int i = 0; i < l; i++) s[i] = rnd.nextInt(r) + 1;
        return s;
    }

    static class ProgressBarRenderer extends JProgressBar implements TableCellRenderer {
        public ProgressBarRenderer(int min, int max) { super(min, max); setStringPainted(true); }
        @Override public Component getTableCellRendererComponent(JTable table, Object value,
                                                                 boolean isSelected, boolean hasFocus, int row, int column) {
            int v = (value instanceof Number) ? ((Number)value).intValue() : 0;
            setValue(v); setString(v + "%"); return this;
        }
    }

    static class BarChartPanel extends JPanel {
        private String title; private Map<String,Double> data = Collections.emptyMap();
        public BarChartPanel(String t) { title = t; setPreferredSize(new Dimension(300,200)); }
        public void setData(Map<String,Double> d) { data = new LinkedHashMap<>(d); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); if (data.isEmpty()) return;
            Graphics2D g2 = (Graphics2D)g; int w=getWidth(), h=getHeight(), pad=40, lp=30;
            double max = Collections.max(data.values()); int bw=(w-2*pad)/data.size(), x=pad;
            g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14)); int tw=g2.getFontMetrics().stringWidth(title);
            g2.drawString(title,(w-tw)/2,pad/2); g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
            for(Map.Entry<String,Double> e:data.entrySet()){double v=e.getValue();int bh=(int)((v/max)*(h-2*pad-lp));int y=h-pad-bh;
                g2.setColor(Color.BLUE);g2.fillRect(x,y,bw-10,bh);
                g2.setColor(Color.BLACK);String lbl=e.getKey();int lw=g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl,x+(bw-10-lw)/2,h-pad+15);
                String vs=String.format("%.1f",v);int vw=g2.getFontMetrics().stringWidth(vs);
                g2.drawString(vs,x+(bw-10-vw)/2,y-5); x+=bw; }
            g2.drawLine(pad,h-pad,w-pad,h-pad);g2.drawLine(pad,pad,pad,h-pad);
        }
    }

    static class PieChartPanel extends JPanel {
        private String title; private Map<String,Double> data = Collections.emptyMap();
        private Color[] cols={Color.RED,Color.GREEN,Color.BLUE,Color.ORANGE,Color.MAGENTA};
        public PieChartPanel(String t){title=t;setPreferredSize(new Dimension(300,200));}
        public void setData(Map<String,Double> d){data=new LinkedHashMap<>(d);}
        @Override protected void paintComponent(Graphics g){
            super.paintComponent(g); if(data.isEmpty()) return;
            Graphics2D g2=(Graphics2D)g; int w=getWidth(),h=getHeight(),pad=20;
            double total=data.values().stream().mapToDouble(x->x).sum();int d=(Math.min(w,h)-2*pad);int x=(w-d)/2,y=(h-d)/2;
            g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14));int tw=g2.getFontMetrics().stringWidth(title);
            g2.drawString(title,(w-tw)/2,pad); int start=0,i=0;
            for(Map.Entry<String,Double> e:data.entrySet()){int angle=(int)Math.round(e.getValue()/total*360);
                g2.setColor(cols[i%cols.length]);g2.fillArc(x,y,d,d,start,angle);start+=angle;i++;}
            g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));int lx=pad,ly=h-pad-data.size()*15;i=0;
            for(Map.Entry<String,Double> e:data.entrySet()){g2.setColor(cols[i%cols.length]);g2.fillRect(lx,ly+i*15,10,10);
                g2.setColor(Color.BLACK);String txt=String.format("%s: %.1f",e.getKey(),e.getValue());g2.drawString(txt,lx+15,ly+i*15+10);i++;}}
    }

    static class StackedBarChartPanel extends JPanel {
        private String title; private Map<String,Double> faultRates = Collections.emptyMap();
        public StackedBarChartPanel(String t) { title = t; setPreferredSize(new Dimension(300,200)); }
        public void setData(Map<String,Double> fr) { faultRates = new LinkedHashMap<>(fr); }
        @Override protected void paintComponent(Graphics g) {
            super.paintComponent(g); if (faultRates.isEmpty()) return;
            Graphics2D g2 = (Graphics2D)g; int w=getWidth(), h=getHeight(), pad=40, lp=30;
            int count = faultRates.size(); int bw = (w-2*pad)/count; int x=pad;
            g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14)); int tw=g2.getFontMetrics().stringWidth(title);
            g2.drawString(title,(w-tw)/2,pad/2);
            g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
            for (Map.Entry<String,Double> e : faultRates.entrySet()) {
                double fr = e.getValue(); double hr = 100 - fr;
                int fh = (int)((fr/100)*(h-2*pad-lp));
                int hh = (int)((hr/100)*(h-2*pad-lp));
                int yBase = h-pad;
                g2.setColor(Color.BLUE);
                g2.fillRect(x, yBase-fh, bw-10, fh);
                g2.setColor(Color.GREEN);
                g2.fillRect(x, yBase-fh-hh, bw-10, hh);
                g2.setColor(Color.BLACK);
                String lbl = e.getKey(); int lw=g2.getFontMetrics().stringWidth(lbl);
                g2.drawString(lbl, x+(bw-10-lw)/2, h-pad+15);
                String txtF = String.format("F:%.1f%%", fr), txtH = String.format("H:%.1f%%", hr);
                int tfw=g2.getFontMetrics().stringWidth(txtF), thw=g2.getFontMetrics().stringWidth(txtH);
                g2.drawString(txtF, x+(bw-10-tfw)/2, yBase-fh+12);
                g2.drawString(txtH, x+(bw-10-thw)/2, yBase-fh-hh-5);
                x += bw;
            }
            g2.setColor(Color.BLACK);
            g2.drawLine(pad,h-pad,w-pad,h-pad);
            g2.drawLine(pad,pad,pad,h-pad);
        }
    }
}
