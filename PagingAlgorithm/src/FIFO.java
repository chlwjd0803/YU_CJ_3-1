import java.util.Arrays;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;

public class FIFO {
    private static final double MEMORY_ACCESS = 0.0002;
    private static final double PAGE_FAULT_OVERHEAD = 8;

    public static PageFaultResult algorithm(int frameCapacity, int[] refStr){
        Queue<Integer> frame = new LinkedList<>(); // FIFO는 Queue의 성질
        HashSet<Integer> exist = new HashSet<>(); // 페이지 존재 여부를 알기 위해 할당
        int curCapacity = 0;
        int pageFault = 0;

//        System.out.println("불러올 페이지들 : " + Arrays.toString(refStr));
//        System.out.println("===============FIFO 알고리즘 결과===============");

        for(int i=0; i<refStr.length; i++){
            if(exist.contains(refStr[i])){
//                System.out.println("현재 프레임 : " + frame.toString() + " pf : " + pageFault);
                continue; // 있으므로 패스
            }
            if(curCapacity >= frameCapacity){ // 없는데 용량이 가득찼을경우
                exist.remove(frame.remove()); // 삭제
                curCapacity--; // 용량 감소
            }
            frame.add(refStr[i]);
            exist.add(refStr[i]);
            curCapacity++; // 새로 추가, 용량 증가
            pageFault++; // 프레임에 없어서 추가한 것이므로 page fault 발생
//            System.out.println("현재 프레임 : " + frame.toString() + " pf : " + pageFault);
        }

        double pageFaultRate = (double)pageFault/refStr.length;
        double eat = (double)(1-pageFaultRate)*MEMORY_ACCESS + pageFaultRate*PAGE_FAULT_OVERHEAD;

//        System.out.println("Page Fault 발생 횟수 : " + pageFault);
//        System.out.println("Page Fault Rate : " + pageFaultRate);
//        System.out.println("EAT : " + eat);

        return new PageFaultResult(pageFault, pageFaultRate, eat);

    }
}
