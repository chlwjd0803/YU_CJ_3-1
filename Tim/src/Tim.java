import java.util.ArrayList;
import java.util.Stack;

public class Tim extends AbstractSort {
    public static int index; // 위치 전역변수
    public static int minrun;
    public static Stack<Comparable[]> stack;


    public static void sort(Comparable[] a){

        while(index < a.length){
            Comparable[] run = new Comparable[a.length];
            int run_len = 0; // 의미없는 초기값
            run[0] = a[index++];
            run[1] = a[index++];
            for(int i = 2; i < a.length && index < a.length; i++){ // 여기서 i가 run의 인덱스
                if(less(run[0],run[1])){
                    if(less(a[index-1], a[index])) run[i] = a[index++];
                    else if(i-1 < minrun){ //minrun의 기준보다 작은지
                        run[i] = a[index++];
                        Insertion.sort(run, i);
                    }
                    else{
                        run_len = i;
                        break;
                    }
                }
                else{
                    if(less(a[index], a[index-1])) run[i] = a[index++];
                    else if(i-1 < minrun){
                        run[i] = a[index++];
                        Insertion.sort_reverse(run, i); //다만 내림차순 기준
                    }
                    else{
                        run_len = i;
                        for(int j=0; j<run_len/2; j++) exch(run, j, run_len-j-1);
                        Tim.show(run);
                        break;
                    }
                }
            }
            Comparable[] temp = new Comparable[run_len];
            for(int i = 0; i < run_len; i++) temp[i] = run[i];
            Tim.show(temp);
            stack.push(temp);
            boolean flag = true;

            while(flag){
                flag = false;
                if(stack.size() >= 3){
                    Comparable[] A = stack.pop();
                    Comparable[] B = stack.pop();
                    Comparable[] C = stack.pop();
                    if(!(C.length > A.length + B.length)){
                        if(C.length > A.length) stack.push(Merge.sort(A,B));
                        else stack.push(Merge.sort(B,C));
                        flag = true; // 병합이 일어난 경우 다시 작업해야하므로
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
                    if(!(B.length > A.length)){
                        stack.push(Merge.sort(A, B));
                        flag = true; // 병합이 일어난 경우 다시 작업해야하므로
                    }
                    else{
                        stack.push(B);
                        stack.push(A);
                    }
                }
            }
        }
        Comparable[] sorted = stack.pop();
        for(int i = 0; i < sorted.length; i++) a[i] = sorted[i];
    }

    public static void main(String[] args) {
        Integer[] a = {10, 13, 9, 15, 18, 21, 13, 8, 5, 11, 3};
        minrun = 4;
        index = 0;
        stack = new Stack<>();
        Tim.sort(a);
        Tim.show(a);
    }

}
