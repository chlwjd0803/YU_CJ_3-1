import java.util.*;

public class OptiLRU {
    private static final double MEMORY_ACCESS = 0.0002;
    private static final double PAGE_FAULT_OVERHEAD = 8;

    public static PageFaultResult algorithm(int frameCapacity, int[] refStr){
        List<Integer> frame = new ArrayList<>();
        HashSet<Integer> exist = new HashSet<>(); // 페이지 존재 여부를 알기 위해 할당
        HashMap<Integer, Integer> far = new HashMap<>();
        Deque<Integer> recent = new LinkedList<>(); // 거리 계산

        int curCapacity = 0;
        int pageFault = 0;
        int farIndex;
//        System.out.println("불러올 페이지들 : " + Arrays.toString(refStr));
//        System.out.println("===============Optimal 알고리즘 결과===============");

        for(int i=0; i<refStr.length; i++){
            if(exist.contains(refStr[i])){
//                System.out.println("현재 프레임 : " + frame.toString() + " pf : " + pageFault);
                recent.remove(Integer.valueOf(refStr[i])); // 해당 원소를 제거해야 하기에
                recent.add(refStr[i]); // 최근에 쓰였으므로 뒤로 옮김
                continue; // 있으므로 패스
            }
            if(curCapacity >= frameCapacity){ // 없는데 용량이 가득찼을경우
//                int[] oldPages = new int[frame.size()/4];
                HashSet<Integer> oldPages = new HashSet<>(); // 가져올 페이지 수는 1/4
                for(int j=0; j<frame.size()/4; j++){
//                    oldPages[j] = recent.removeFirst();
                    oldPages.add(recent.removeFirst());
                }
                // old Page들을 들고옴

                far.clear(); // 거리 초기화
                Iterator<Integer> iter = recent.iterator();
                int recIndex = 0;
                for(int j=i+1; j<refStr.length; j++) { // j-i는 거리
                    Integer value = iter.next(); // 이거 수정해야할듯
                    if (far.get(value) == null)
                        far.put(refStr[j], j - i); //거리를 저장
                    if(recIndex>frame.size()/4 - 1) break;
                }
                farIndex = 0; // 일단 0번째로 초기화
                for(int j=0; j<frame.size(); j++){
                    if(far.get(frame.get(j)) == null){ // 대상이 이후에 계속 존재하지 않으면 그냥 지움
                        farIndex = j;
                        break;
                    }
                    if(far.get(frame.get(j)) > far.get(frame.get(farIndex)))
                        farIndex = j; // 가장 먼 거리를 가진 index 찾아 넣기
                }

                exist.remove(frame.remove(farIndex)); // 삭제
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
