import javax.swing.*;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.DefaultTableModel;
import java.awt.*;
import java.awt.event.*;
import java.util.*;
import java.util.List;

/**
 * Enhanced Swing UI for page replacement simulation with bar and pie charts.
 * Uses FIFO.algorithm(), LRU.algorithm(), Optimal.algorithm().
 */
public class PageReplacementUI extends JFrame {
    private JTextField tfCapacity, tfLength, tfRange;
    private JSpinner spRuns;
    private JButton btnRun;
    private JTable table;
    private DefaultTableModel tableModel;
    private JTabbedPane tabbedPane;

    // Chart panels
    private BarChartPanel faultBar;
    private BarChartPanel eatBar;
    private PieChartPanel faultPie;
    private PieChartPanel ratePie;

    public PageReplacementUI() {
        super("Page Replacement Simulator");
        setDefaultCloseOperation(EXIT_ON_CLOSE);
        setLayout(new BorderLayout(10, 10));

        // Input
        JPanel input = new JPanel(new GridLayout(4,2,5,5));
        input.setBorder(BorderFactory.createTitledBorder("Params"));
        input.add(new JLabel("Frame Capacity:")); tfCapacity=new JTextField("10"); input.add(tfCapacity);
        input.add(new JLabel("Ref String Length:")); tfLength=new JTextField("500"); input.add(tfLength);
        input.add(new JLabel("Page Range (1~n):")); tfRange=new JTextField("50"); input.add(tfRange);
        input.add(new JLabel("Runs:")); spRuns=new JSpinner(new SpinnerNumberModel(1,1,100,1)); input.add(spRuns);
        add(input, BorderLayout.NORTH);

        // Table
        String[] cols={"Run","Algo","Faults","Rate(%)","EAT(ms)"};
        tableModel=new DefaultTableModel(cols,0){@Override public boolean isCellEditable(int r,int c){return false;}};
        table=new JTable(tableModel);
        JScrollPane tblPane=new JScrollPane(table);
        table.setFillsViewportHeight(true);
        table.setShowGrid(false);
        table.setIntercellSpacing(new Dimension(0, 0));
        table.setDefaultRenderer(Object.class, new DefaultTableCellRenderer() {
            @Override
            public Component getTableCellRendererComponent(JTable table, Object value,
                                                           boolean isSelected, boolean hasFocus,
                                                           int row, int column) {
                Component c = super.getTableCellRendererComponent(
                        table, value, isSelected, hasFocus, row, column);
                if (!isSelected) {
                    c.setBackground((row % 2 == 0)
                            ? Color.WHITE
                            : new Color(245, 245, 245));
                }
                setHorizontalAlignment(column > 1
                        ? SwingConstants.CENTER
                        : SwingConstants.LEFT);
                return c;
            }
        });
// 헤더 스타일
        table.getTableHeader().setFont(new Font("SansSerif", Font.BOLD, 12));
        table.setRowHeight(24);

        // Charts
        faultBar=new BarChartPanel("Avg Page Faults");
        eatBar  =new BarChartPanel("Avg EAT (ms)");
        faultPie=new PieChartPanel("Fault Distribution");
        ratePie =new PieChartPanel("Rate Distribution");
        JPanel chartGrid=new JPanel(new GridLayout(2,2,5,5));
        chartGrid.add(faultBar);
        chartGrid.add(faultPie);
        chartGrid.add(eatBar);
        chartGrid.add(ratePie);

        // Tabs
        tabbedPane=new JTabbedPane();
        tabbedPane.addTab("Table", tblPane);
        tabbedPane.addTab("Charts", chartGrid);
        add(tabbedPane, BorderLayout.CENTER);

        // Run
        btnRun=new JButton("Run"); btnRun.addActionListener(e->runSimulation());
        JPanel south=new JPanel(); south.add(btnRun); add(south, BorderLayout.SOUTH);

        pack(); setLocationRelativeTo(null); setVisible(true);
    }

    private void runSimulation(){
        tableModel.setRowCount(0);
        faultBar.setData(Collections.emptyMap());
        eatBar.setData(Collections.emptyMap());
        faultPie.setData(Collections.emptyMap());
        ratePie.setData(Collections.emptyMap());

        int cap=Integer.parseInt(tfCapacity.getText());
        int len=Integer.parseInt(tfLength.getText());
        int range=Integer.parseInt(tfRange.getText());
        int runs=(Integer)spRuns.getValue();

        Map<String, List<Integer>> fm=new LinkedHashMap<>();
        Map<String,List<Double>> em=new LinkedHashMap<>();
        Map<String,List<Double>> rm=new LinkedHashMap<>();
        for(String a:Arrays.asList("FIFO","LRU","OPT")){fm.put(a,new ArrayList<>());em.put(a,new ArrayList<>());rm.put(a,new ArrayList<>());}

        for(int i=1;i<=runs;i++){
            int[] ref=generateRandomSequence(len,range);
            PageFaultResult r1=FIFO.algorithm(cap,ref);
            PageFaultResult r2=LRU.algorithm(cap,ref);
            PageFaultResult r3=Optimal.algorithm(cap,ref);
            List<PageFaultResult> rs=Arrays.asList(r1,r2,r3);
            String[] names={"FIFO","LRU","OPT"};
            for(int j=0;j<3;j++){
                PageFaultResult r=rs.get(j);
                String nm=names[j];
                tableModel.addRow(new Object[]{i,nm,r.faults,String.format("%.2f",r.rate*100),String.format("%.4f",r.eat)});
                fm.get(nm).add(r.faults);
                em.get(nm).add(r.eat);
                rm.get(nm).add(r.rate);
            }
        }
        // averages
        Map<String,Double> avgF=new LinkedHashMap<>();
        Map<String,Double> avgE=new LinkedHashMap<>();
        Map<String,Double> avgR=new LinkedHashMap<>();
        for(String a:fm.keySet()){
            avgF.put(a,fm.get(a).stream().mapToInt(x->x).average().orElse(0));
            avgE.put(a,em.get(a).stream().mapToDouble(x->x).average().orElse(0));
            avgR.put(a,rm.get(a).stream().mapToDouble(x->x).average().orElse(0));
        }
        faultBar.setData(avgF); eatBar.setData(avgE);
        faultPie.setData(avgF);  ratePie.setData(avgR);
        faultBar.repaint(); eatBar.repaint(); faultPie.repaint(); ratePie.repaint();
    }

    private int[] generateRandomSequence(int l,int r){int[]s=new int[l];Random rnd=new Random();for(int i=0;i<l;i++)s[i]=rnd.nextInt(r)+1;return s;}

    public static void main(String[]a){SwingUtilities.invokeLater(PageReplacementUI::new);}

    // Java2D Bar Chart
    static class BarChartPanel extends JPanel{
        private String title; private Map<String,Double> data=Collections.emptyMap();
        public BarChartPanel(String t){title=t;setPreferredSize(new Dimension(300,200));}
        public void setData(Map<String,Double>d){data=new LinkedHashMap<>(d);}
        @Override protected void paintComponent(Graphics g){super.paintComponent(g);if(data.isEmpty())return;Graphics2D g2=(Graphics2D)g;int w=getWidth(),h=getHeight(),pad=40,lp=30; double max=Collections.max(data.values());int bw=(w-2*pad)/data.size();int x=pad;g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14));int tw=g2.getFontMetrics().stringWidth(title);g2.drawString(title,(w-tw)/2,pad/2);
            g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));
            for(Map.Entry<String,Double>e:data.entrySet()){double v=e.getValue();int bh=(int)((v/max)*(h-2*pad-lp));int y=h-pad-bh;g2.setColor(Color.BLUE);g2.fillRect(x,y,bw-10,bh);g2.setColor(Color.BLACK);String lbl=e.getKey();int lw=g2.getFontMetrics().stringWidth(lbl);g2.drawString(lbl,x+(bw-10-lw)/2,h-pad+15);String vs=String.format("%.1f",v);int wv=g2.getFontMetrics().stringWidth(vs);g2.drawString(vs,x+(bw-10-wv)/2,y-5);x+=bw;}g2.drawLine(pad,h-pad,w-pad,h-pad);g2.drawLine(pad,pad,pad,h-pad);}    }

    // Java2D Pie Chart
    static class PieChartPanel extends JPanel{
        private String title; private Map<String,Double> data=Collections.emptyMap();
        private Color[] cols={Color.RED,Color.GREEN,Color.BLUE,Color.ORANGE,Color.MAGENTA};
        public PieChartPanel(String t){title=t;setPreferredSize(new Dimension(300,200));}
        public void setData(Map<String,Double>d){data=new LinkedHashMap<>(d);}
        @Override protected void paintComponent(Graphics g){super.paintComponent(g);if(data.isEmpty())return;Graphics2D g2=(Graphics2D)g;int w=getWidth(),h=getHeight(),pad=20;double total=data.values().stream().mapToDouble(x->x).sum();int diameter=Math.min(w,h)-2*pad;int x=(w-diameter)/2,y=(h-diameter)/2;g2.setFont(new Font(Font.SANS_SERIF,Font.BOLD,14));int tw=g2.getFontMetrics().stringWidth(title);g2.drawString(title,(w-tw)/2,pad);
            int start=0,i=0;for(Map.Entry<String,Double>e:data.entrySet()){double v=e.getValue();int angle=(int)Math.round(v/total*360);g2.setColor(cols[i%cols.length]);g2.fillArc(x,y,diameter,diameter,start,angle);start+=angle;i++;}
            // legend
            g2.setFont(new Font(Font.SANS_SERIF,Font.PLAIN,12));int lx=pad,ly=h-pad-((data.size())*15);
            i=0;for(Map.Entry<String,Double>e:data.entrySet()){g2.setColor(cols[i%cols.length]);g2.fillRect(lx,ly+ i*15,10,10);g2.setColor(Color.BLACK);
                String txt=String.format("%s: %.1f",e.getKey(),e.getValue());g2.drawString(txt,lx+15,ly+ i*15+10);i++;}
        }
    }
}