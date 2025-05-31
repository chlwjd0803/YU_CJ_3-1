import javax.swing.*;
import javax.swing.border.EmptyBorder;
import java.awt.*;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.*;
import java.util.List;
import javax.imageio.ImageIO;

public class MemoryPolicySimulator {
    public static void main(String[] args) {
        SwingUtilities.invokeLater(MainFrame::new);
    }

    /* ─────────────────────────────  MODEL  ───────────────────────────── */
    public static final class Page {
        public enum Status { HIT, PAGE_FAULT, MIGRATION }
        public final int loc;                 // 1-based frame 위치 (1 = 왼쪽 첫 칸)
        public final char cur;                // 현재 참조 페이지
        public final Status status;           // Hit / Page_Fault / Migration
        public final List<Character> snapshot;// 교체 후 프레임 상태 스냅샷 (왼→오른 순)
        Page(int loc,char cur,Status st,List<Character> snap){ this.loc=loc; this.cur=cur; this.status=st; this.snapshot=snap; }
    }

    /* ────────────────────────────  CORE  ───────────────────────────── */
    public static final class Core {
        private final int cap;
        private final Deque<Character> frame = new ArrayDeque<>();
        private final Deque<Character> recent = new ArrayDeque<>();
        private final Set<Character> exist = new HashSet<>();
        public final List<Page> hist = new ArrayList<>();
        public int hit,fault,mig;
        public Core(int cap){ this.cap=cap; }

        public void operate(char cur,String policy,char[] ref,int idx){
            boolean isHit = exist.contains(cur);
            Page.Status st; int loc;
            if(isHit){
                st = Page.Status.HIT; hit++;
                if(isLRU(policy)){ recent.remove(cur); recent.addLast(cur);}
                loc=indexOf(frame,cur)+1;
            }else{
                if(frame.size()>=cap){
                    char victim = switch(policy){
                        case "FIFO" -> frame.removeFirst();
                        case "LRU"  -> { char v=recent.removeFirst(); frame.remove(v); yield v; }
                        case "HOL" -> { char v=optlruVictim(ref,idx); frame.remove(v); recent.remove(v); yield v; }
                        default -> { char v=optimalVictim(ref,idx); frame.remove(v); yield v; }
                    };
                    exist.remove(victim); mig++; fault++; st=Page.Status.MIGRATION;
                }else{ fault++; st=Page.Status.PAGE_FAULT; }
                frame.addLast(cur); exist.add(cur);
                if(isLRU(policy)) recent.addLast(cur);
                loc=frame.size();
            }
            hist.add(new Page(loc,cur,st,new ArrayList<>(frame)));
        }

        private static boolean isLRU(String p){
            // "LRU" 또는 "HOL"을 LRU 계열로 간주
            return "LRU".equals(p) || "HOL".equals(p);
        }

        private static int indexOf(Deque<Character> dq,char t){
            int i=0;
            for(char c:dq){
                if(c==t) return i;
                i++;
            }
            return -1;
        }

        private char optimalVictim(char[] ref,int idx){
            int far=-1; char vic=frame.peekFirst();
            for(char p:frame){
                int nxt=ref.length;
                for(int j=idx+1;j<ref.length;j++){
                    if(ref[j]==p){
                        nxt=j;
                        break;
                    }
                }
                if(nxt>far){
                    far=nxt;
                    vic=p;
                }
            }
            return vic;
        }

        private char optlruVictim(char[] ref,int idx){
            // 오래된 페이지 중 절반(half of recent size)을 후보로 삼아
            List<Character> old=new ArrayList<>();
            Iterator<Character> it=recent.iterator();
            for(int i=0;i<recent.size()/2 && it.hasNext(); i++){
                old.add(it.next());
            }

            // 후보들의 미래 참조 거리를 계산
            Map<Character,Integer> dist=new HashMap<>();
            int collected=0;
            for(int j=idx+1; j<ref.length && collected<old.size(); j++){
                char p=ref[j];
                if(old.contains(p) && !dist.containsKey(p)){
                    dist.put(p, j - idx);
                    collected++;
                }
            }

            // 미래에 가장 늦게 참조되거나, 참조되지 않는 페이지를 교체대상 지정하기
            char vic='?';
            for(char p:old){
                if(!dist.containsKey(p)){
                    vic = p;
                    break;
                }
                if(vic=='?' || dist.get(p) > dist.get(vic)){
                    vic = p;
                }
            }

            // 만약 후보 중에 참조되지 않는 페이지가 없다면,
            // 그냥 프레임 맨 앞(기본 FIFO 방식) 페이지를 내보냄
            return vic=='?' ? frame.peekFirst() : vic;
        }
    }

    /* ─────────────────────────────  UI  ───────────────────────────── */
    private static final class MainFrame extends JFrame{
        private final JTextField tfRef=new JTextField("234343552231", 30);
        private final JTextField tfCap=new JTextField("3",4);
        // 콤보박스에 "HOL"을 추가
        private final JComboBox<String> cbPol=new JComboBox<>(new String[]{"FIFO","LRU","Optimal","HOL"});
        private final JTextArea log=new JTextArea(8,30);
        private final TimelinePanel timeline=new TimelinePanel();
        private final PiePanel pie=new PiePanel();

        MainFrame(){
            super("메모리 페이지 교체 시뮬레이터 (샘플코드 JAVA 변환)");
            setDefaultCloseOperation(EXIT_ON_CLOSE);
            setLayout(new BorderLayout(8,8));
            add(buildControls(),BorderLayout.NORTH);
            add(new JScrollPane(timeline),BorderLayout.CENTER);
            add(pie,BorderLayout.EAST);
            add(new JScrollPane(log),BorderLayout.SOUTH);
            log.setEditable(false);
            pack(); setLocationRelativeTo(null); setVisible(true);
        }

        private JPanel buildControls(){
            JButton run=new JButton("실행"), rnd=new JButton("랜덤"), save=new JButton("PNG 저장");
            run.addActionListener(e->runSim());
            rnd.addActionListener(e->tfRef.setText(randomRef()));
            save.addActionListener(e->timeline.exportPNG(this));

            JPanel p=new JPanel();
            p.add(new JLabel("정책"));    p.add(cbPol);
            p.add(new JLabel("참조열"));  p.add(tfRef);
            p.add(new JLabel("프레임 수")); p.add(tfCap);
            p.add(rnd); p.add(run); p.add(save);
            return p;
        }

        private String randomRef(){
            Random r=new Random();
            int len=r.nextInt(45)+5;
            StringBuilder sb=new StringBuilder();
            for(int i=0; i<len; i++){
                sb.append((char)('A' + r.nextInt(26)));
            }
            return sb.toString();
        }

        private void runSim(){
            log.setText("");
            String refs = tfRef.getText().trim().toUpperCase();
            if(refs.isEmpty()) return;

            int cap;
            try{
                cap=Integer.parseInt(tfCap.getText().trim());
            }catch(NumberFormatException ex){
                return;
            }

            Core core=new Core(cap);
            char[] arr = refs.toCharArray();
            for(int i=0; i<arr.length; i++){
                core.operate(arr[i], cbPol.getSelectedItem().toString(), arr, i);
                log.append("데이터 " + arr[i] + " → " + core.hist.get(i).status + "\n");
            }

            timeline.render(core.hist, cap);
            pie.set(core.hit, core.fault);
            log.append(String.format("Page Fault Rate: %.2f%%\n", (core.fault * 100.0) / (core.hit + core.fault)));
        }
    }

    private static final class TimelinePanel extends JPanel {
        private BufferedImage buf = new BufferedImage(2048, 2048, BufferedImage.TYPE_INT_ARGB);

        TimelinePanel() {
            setPreferredSize(new Dimension(900, 600));
            setBorder(new EmptyBorder(5, 5, 5, 5));
        }

        void render(List<Page> hist, int cap){
            Graphics2D g = buf.createGraphics();
            g.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);
            g.setColor(Color.WHITE);
            g.fillRect(0, 0, buf.getWidth(), buf.getHeight());

            int cell = 30, gap = 5;
            // ── 상단 헤더: 프레임 번호 1..cap ──
            for(int f=1; f<=cap; f++){
                drawCell(g, f, 0, cell, gap, new Color(200,200,200), Color.BLACK, String.valueOf(f));
            }
            // ── 각 시간 행 ──
            for(int t=0; t<hist.size(); t++){
                Page pg = hist.get(t);
                int row = t+1;
                // 좌측 헤더: 현재 참조 페이지 문자
                drawCell(g, 0, row, cell, gap, new Color(230,230,230), Color.BLACK, String.valueOf(pg.cur));
                for(int f=1; f<=cap; f++){
                    String ch = (f <= pg.snapshot.size()) ? String.valueOf(pg.snapshot.get(f-1)) : "";
                    boolean hi = (f == pg.loc);
                    Color fill = hi ? switch(pg.status){
                        case HIT -> new Color(0,200,0,200);
                        case PAGE_FAULT -> new Color(220,0,0,200);
                        default -> new Color(150,0,150,200);
                    } : new Color(245,245,245);
                    Color text = hi ? Color.WHITE : Color.BLACK;
                    drawCell(g, f, row, cell, gap, fill, text, ch);
                }
            }
            g.dispose();

            int width = (cap + 1) * (cell + gap);
            int height = (hist.size() + 1) * (cell + gap);
            setPreferredSize(new Dimension(Math.max(900, width), Math.max(600, height)));
            revalidate();
            repaint();
        }

        private void drawCell(Graphics2D g, int col, int row, int c, int gap, Color fill, Color txtCol, String txt){
            int x = col * (c + gap), y = row * (c + gap);
            g.setColor(fill);
            g.fillRect(x, y, c, c);
            g.setColor(Color.GRAY);
            g.drawRect(x, y, c, c);
            if(txt != null && !txt.isEmpty()){
                g.setColor(txtCol);
                g.drawString(txt, x + c*0.3f, y + c*0.7f);
            }
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            g.drawImage(buf, 0, 0, null);
        }

        void exportPNG(Component parent){
            JFileChooser fc = new JFileChooser();
            fc.setSelectedFile(new File("timeline.png"));
            if(fc.showSaveDialog(parent) == JFileChooser.APPROVE_OPTION){
                try{
                    ImageIO.write(buf, "png", fc.getSelectedFile());
                }catch(IOException ex){
                    ex.printStackTrace();
                }
            }
        }
    }

    private static final class PiePanel extends JPanel {
        int hit, fault;
        PiePanel(){
            setPreferredSize(new Dimension(280, 280));
            setBorder(new EmptyBorder(10,10,10,10));
        }

        void set(int h,int f){
            hit = h;
            fault = f;
            repaint();
        }

        @Override
        protected void paintComponent(Graphics g){
            super.paintComponent(g);
            if(hit + fault == 0) return;

            Graphics2D g2 = (Graphics2D) g;
            g2.setRenderingHint(RenderingHints.KEY_ANTIALIASING, RenderingHints.VALUE_ANTIALIAS_ON);

            int d = Math.min(getWidth(), getHeight()) - 80;
            int cx = 30, cy = 10;
            double angHit = 360.0 * hit / (hit + fault);

            g2.setColor(Color.GREEN);
            g2.fillArc(cx, cy, d, d, 0, (int) angHit);
            g2.setColor(Color.RED);
            g2.fillArc(cx, cy, d, d, (int) angHit, (int) (360 - angHit));


            int lx = cx, ly = cy + d + 20;
            g2.setColor(Color.GREEN);
            g2.fillRect(lx, ly, 15, 15);
            g2.setColor(Color.BLACK);
            g2.drawRect(lx, ly, 15, 15);
            g2.drawString("Hit", lx + 20, ly + 12);

            ly += 20;
            g2.setColor(Color.RED);
            g2.fillRect(lx, ly, 15, 15);
            g2.setColor(Color.BLACK);
            g2.drawRect(lx, ly, 15, 15);
            g2.drawString("Fault", lx + 20, ly + 12);

            ly += 30;
            g2.drawString("Hit: " + hit, lx, ly);
            g2.drawString("Fault: " + fault, lx, ly + 15);
        }
    }
}
