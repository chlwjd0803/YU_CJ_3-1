import java.util.Stack;

public class Tim extends AbstractSort {
    public static int index; // 원본배열 위치 index 필드
    public static int minrun;
    public static Stack<Comparable[]> stack; // runs를 담을 Stack


    public static void sort(Comparable[] a){

        while(index < a.length){ // 원본 배열의 index가 전체를 다 탐색할때까지 시행, 블록 안에서도 검사 필요
            Comparable[] run = new Comparable[a.length];
            int run_len = 0; // 의미없는 초기값
            run[0] = a[index++];
            run[1] = a[index++]; // 일단 두 원소를 담고 오름차순인지 내림차순인지 검사
            for(int i = 2; i < a.length && index < a.length; i++, run_len = i){ // 여기서 i가 run의 인덱스, run_len을 지속적으로 최신화
                if(less(run[0],run[1])){ // 오름차순 이라면
                    if(less(a[index-1], a[index])) run[i] = a[index++]; // 대소관계가 정렬이 필요없을 경우
                    else if(i-1 < minrun){ //minrun의 기준보다 작은지
                        run[i] = a[index++];
                        Insertion.sort(run, i); // 해당 원소를 삽입정렬
                    }
                    else break;
                }
                else{
                    if(less(a[index], a[index-1])) run[i] = a[index++];
                    else if(i-1 < minrun){
                        run[i] = a[index++];
                        Insertion.sort_reverse(run, i); // 내림차순 기준 삽입정렬
                    }
                    else break;
                }
            }
            // 내림차순 정렬인 run을 오름차순으로 변경해줌
            if(less(run[1],run[0])) for(int j=0; j<run_len/2; j++) exch(run, j, run_len-j-1);

            Comparable[] temp = new Comparable[run_len];
            // run의 size를 맞추어 Stack에 저장하기 위함
            for(int i = 0; i < run_len; i++) temp[i] = run[i];
            stack.push(temp);

            // Merge Sort 조건을 위한 flag
            boolean flag = true;

            while(flag){
                flag = false;
                if(stack.size() >= 3){
                    Comparable[] A = stack.pop();
                    Comparable[] B = stack.pop();
                    Comparable[] C = stack.pop();
                    if(!(C.length > A.length + B.length)){ // 조건1을 위반하는지 검사
                        if(C.length > A.length) stack.push(Merge.sort(A,B));
                        else stack.push(Merge.sort(B,C));
                        flag = true; // 병합이 일어난 경우 다시 병합 검사를 해야함
                    }
                    else{
                        stack.push(C);
                        stack.push(B);
                        stack.push(A);
                    }
                }
                if(stack.size() >= 2){
                    Comparable[] A = stack.pop();
                    Comparable[] B = stack.pop();
                    // 조건2를 위반하는지 검사
                    // 원소가 2개일때 배열은 모두 검사했다면 바로 병합 -> 1개의 run만 남을것임.
                    if(!(B.length > A.length) || index == a.length){
                        stack.push(Merge.sort(A, B));
                        flag = true;
                    }
                    else{
                        stack.push(B);
                        stack.push(A);
                    }
                }
            }
        }
        Comparable[] sorted = stack.pop(); //원소가 하나만 남아야함
        for(int i = 0; i < sorted.length; i++) a[i] = sorted[i];
    }

    public static void main(String[] args) {
        Integer[] a = {10, 13, 9, 15, 18, 21, 13, 8, 5, 11, 3};
        minrun = 4; // minrun은 자료가 충분히 커졌을때 동적으로 변경가능, 해당예시에서는 고정함.
        index = 0;
        stack = new Stack<>();
        Tim.show(a); // 정렬 전
        Tim.sort(a);
        Tim.show(a); // 정렬 후
    }

}
