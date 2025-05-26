import java.awt.image.ImageProducer;
import java.util.*;

public class Optimal {
    private static final double MEMORY_ACCESS = 0.0002;
    private static final double PAGE_FAULT_OVERHEAD = 8;

    public static void algorithm(int frameCapacity, int[] refStr){
        List<Integer> frame = new ArrayList<>(); // FIFO는 Queue의 성질
        HashSet<Integer> exist = new HashSet<>(); // 페이지 존재 여부를 알기 위해 할당
        HashMap<Integer, Integer> far = new HashMap<>();
        int curCapacity = 0;
        int pageFault = 0;
        int maxIndex;

        for(int i=0; i<refStr.length; i++){
            if(exist.contains(refStr[i])) continue; // 있으므로 패스
            if(curCapacity >= frameCapacity){ // 없는데 용량이 가득찼을경우
                far.clear(); // 거리 초기화
                for(int j=i+1; j<refStr.length; j++) if(exist.contains(refStr[j]))
                    far.put(refStr[j], j-i); //거리를 저장

                maxIndex = -1;
                for(int j=0; j<frame.size(); j++){
                    if(far.get(frame.get(j)) == null){ // 대상이 이후에 계속 존재하지 않으면 그냥 지움
                        maxIndex = j;
                        break;
                    }
                    if(far.get(frame.get(j)) > far.get(frame.get(maxIndex))) // 이부분 잘못됨
                        maxIndex = frame.get(j); // 가장 먼 거리를 가진 index 찾아 넣기
                }

                exist.remove(frame.remove(maxIndex)); // 삭제
                curCapacity--; // 용량 감소
            }
            frame.add(refStr[i]);
            exist.add(refStr[i]);
            curCapacity++; // 새로 추가, 용량 증가
            pageFault++; // 프레임에 없어서 추가한 것이므로 page fault 발생
        }
        double pageFaultRate = (double)pageFault/refStr.length;
        double eat = (double)(1-pageFaultRate)*MEMORY_ACCESS + pageFaultRate*PAGE_FAULT_OVERHEAD;

        System.out.println("Page Fault 발생 횟수 : " + pageFault);
        System.out.println("Page Fault Rate : " + pageFaultRate);
        System.out.println("EAT : " + eat);

    }
}
