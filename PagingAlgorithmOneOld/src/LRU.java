import java.util.*;

public class LRU {
    private static final double MEMORY_ACCESS = 0.0002;
    private static final double PAGE_FAULT_OVERHEAD = 8;

    public static void algorithm(int frameCapacity, int[] refStr){
        List<Integer> frame = new ArrayList<>();
        HashSet<Integer> exist = new HashSet<>(); // 페이지 존재 여부를 알기 위해 할당
        Deque<Integer> recent = new LinkedList<>(); // 거리 계산

        int curCapacity = 0;
        int pageFault = 0;
        System.out.println("불러올 페이지들 : " + Arrays.toString(refStr));
        System.out.println("===============LRU 알고리즘 결과===============");

        for(int i=0; i<refStr.length; i++){
            if(exist.contains(refStr[i])){
                System.out.println("현재 프레임 : " + frame.toString() + " pf : " + pageFault);
                recent.remove(Integer.valueOf(refStr[i])); // 해당 원소를 제거해야 하기에
                recent.add(refStr[i]); // 최근에 쓰였으므로 뒤로 옮김
                continue; // 있으므로 패스
            }
            if(curCapacity >= frameCapacity){ // 없는데 용량이 가득찼을경우
                int oldPage = recent.removeFirst();
                frame.remove(Integer.valueOf(oldPage));
                exist.remove(oldPage);
                curCapacity--;
            }
            frame.add(refStr[i]);
            exist.add(refStr[i]);
            recent.add(refStr[i]);
            curCapacity++; // 새로 추가, 용량 증가
            pageFault++; // 프레임에 없어서 추가한 것이므로 page fault 발생
            System.out.println("현재 프레임 : " + frame.toString() + " pf : " + pageFault);
        }

        double pageFaultRate = (double)pageFault/refStr.length;
        double eat = (double)(1-pageFaultRate)*MEMORY_ACCESS + pageFaultRate*PAGE_FAULT_OVERHEAD;

        System.out.println("Page Fault 발생 횟수 : " + pageFault);
        System.out.println("Page Fault Rate : " + pageFaultRate);
        System.out.println("EAT : " + eat);


    }
}
