import java.util.*;

public class OptiLRU {
    private static final double MEMORY_ACCESS = 0.0002;
    private static final double PAGE_FAULT_OVERHEAD = 8;

    public static void algorithm(int frameCapacity, int[] refStr){
        List<Integer> frame = new ArrayList<>(); // 프레임
        HashSet<Integer> exist = new HashSet<>(); // 페이지 존재 여부를 알기 위해 할당
        HashMap<Integer, Integer> far = new HashMap<>(); // 이후의 거리 계산
        Deque<Integer> recent = new LinkedList<>(); // 오래 전 참조 ~ 최근 참조

        int curCapacity = 0;
        int pageFault = 0;
        Integer farpage;
        System.out.println("불러올 페이지들 : " + Arrays.toString(refStr));
        System.out.println("===============OptiLRU 알고리즘 결과===============");

        for(int i=0; i<refStr.length; i++){
            if(exist.contains(refStr[i])){
                System.out.println("현재 프레임 : " + frame.toString() + " pf : " + pageFault);
                recent.remove(Integer.valueOf(refStr[i])); // 해당 원소를 제거해야 하기에
                recent.add(refStr[i]); // 최근에 쓰였으므로 뒤로 옮김
                continue; // 있으므로 패스
            }
            if(curCapacity >= frameCapacity){ // 없는데 용량이 가득찼을경우
                far.clear(); // 거리 초기화

                Iterator<Integer> iter = recent.iterator();
                List<Integer> oldPages = new ArrayList<>();

                for(int j=0; j<recent.size()/2; j++){
                    oldPages.add(iter.next()); // 1/2 후보만 저장
                }

                int recIndex = 0;
                // 이후의 배열을 모두 검사하되, 최소 거리들을 모두 찾는다면 종료
                for(int j=i+1; j<refStr.length && recIndex < oldPages.size(); j++) { // j-i는 거리
                    if (oldPages.contains(refStr[j]) && far.get(refStr[j]) == null){
                        far.put(refStr[j], j - i); //거리를 저장
                        recIndex++;
                    }
                }
                farpage = -1; // 없을때의 값

                iter = oldPages.iterator(); // 오래된 페이지 순서대로 iterator
                while(iter.hasNext()){ // 이미 비었으면 그냥 넘어가는거 아닌가 싶네
                    Integer page = iter.next();
                    if(farpage == -1) farpage = page; // 처음값이면 그냥 넣기
                    if(far.get(page) == null){ // 이후에 참조하지 않으므로
                        farpage = page;
                        break;
                    }  // null 방지를 위해 조건문을 나눔
                    if(far.get(page) > far.get(farpage)) farpage = page;
                }

                if(farpage == -1){ // 모든 페이지가 참조되지 않을때
                    Integer target = frame.remove(0);
                    exist.remove(target);
                    recent.remove(target);
                    // FIFO와 같이 첫번째 인덱스 제거
                }
                else{ // 골라낸 페이지를 제거
                    frame.remove(farpage);
                    exist.remove(farpage);
                    recent.remove(farpage);
                }
                curCapacity--; // 용량 감소
            }
            frame.add(refStr[i]);
            exist.add(refStr[i]);
            recent.add(Integer.valueOf(refStr[i]));
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
