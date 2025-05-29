import javax.swing.JTextArea;
import java.util.Arrays;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;

public class PageFaultResult {
    public final int faults;
    public final double rate;
    public final double eat;

    public PageFaultResult(int faults, double rate, double eat) {
        this.faults = faults;
        this.rate   = rate;
        this.eat    = eat;
    }

    /**
     * 주어진 알고리즘에 따라 단계별 시뮬레이션을 실행하고,
     * 메모리 진입/퇴출 이벤트와 프레임 상태를 JTextArea에 출력합니다.
     * 최종 요약은 각 알고리즘 클래스의 algorithm() 메서드를 호출하여 가져옵니다.
     */
    public static void runWithStates(String[] refStrArr, int capacity, String algo, JTextArea area) {
        int[] refStr = Arrays.stream(refStrArr).mapToInt(Integer::parseInt).toArray();

        area.append("=== Simulation Start: " + algo + " ===\n\n");

        List<Integer> frame = new ArrayList<>();
        Set<Integer> exist = new HashSet<>();
        Deque<Integer> recent = new LinkedList<>();

        for (int i = 0; i < refStr.length; i++) {
            int page = refStr[i];
            area.append("DATA " + page);

            if ("FIFO".equals(algo)) {
                if (exist.contains(page)) {
                    area.append(" is Hit\n");
                } else {
                    if (frame.size() >= capacity) {
                        int evicted = frame.remove(0);
                        exist.remove(evicted);
                        area.append(" is Migration (Evict " + evicted + ", Load " + page + ")\n");
                    } else {
                        area.append(" is Page Fault (Load " + page + ")\n");
                    }
                    frame.add(page);
                    exist.add(page);
                }
            } else if ("LRU".equals(algo)) {
                if (exist.contains(page)) {
                    area.append(" is Hit\n");
                    recent.remove(page);
                    recent.addLast(page);
                } else {
                    if (frame.size() >= capacity) {
                        int lruPage = recent.pollFirst();
                        frame.remove(Integer.valueOf(lruPage));
                        exist.remove(lruPage);
                        area.append(" is Migration (Evict " + lruPage + ", Load " + page + ")\n");
                    } else {
                        area.append(" is Page Fault (Load " + page + ")\n");
                    }
                    frame.add(page);
                    exist.add(page);
                    recent.addLast(page);
                }
            } else if ("Optimal".equals(algo)) {
                if (exist.contains(page)) {
                    area.append(" is Hit\n");
                } else {
                    if (frame.size() >= capacity) {
                        int evictPage = frame.get(0);
                        int farthest = -1;
                        for (int p : frame) {
                            int nextUse = refStr.length;
                            for (int j = i + 1; j < refStr.length; j++) {
                                if (refStr[j] == p) { nextUse = j; break; }
                            }
                            if (nextUse > farthest) {
                                farthest = nextUse;
                                evictPage = p;
                            }
                        }
                        frame.remove(Integer.valueOf(evictPage));
                        exist.remove(evictPage);
                        area.append(" is Migration (Evict " + evictPage + ", Load " + page + ")\n");
                    } else {
                        area.append(" is Page Fault (Load " + page + ")\n");
                    }
                    frame.add(page);
                    exist.add(page);
                }
            } else {
                area.append(" Unknown Algorithm\n");
            }

            area.append("Frames: " + frame + "\n\n");
        }

        // Summary via algorithm classes
        PageFaultResult summary;
        switch (algo) {
            case "FIFO":
                summary = FIFO.algorithm(capacity, refStr);
                break;
            case "LRU":
                summary = LRU.algorithm(capacity, refStr);
                break;
            case "Optimal":
                summary = Optimal.algorithm(capacity, refStr);
                break;
            default:
                summary = FIFO.algorithm(capacity, refStr);
        }

        area.append("=== Summary ===\n");
        area.append("Algorithm: " + algo + "\n");
        area.append("Faults: " + summary.faults + "\n");
        area.append(String.format("Page Fault Rate: %.2f%%\n", summary.rate * 100));
        area.append(String.format("EAT: %.5f ms\n", summary.eat));
    }
}
