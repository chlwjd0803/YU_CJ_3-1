import java.util.ArrayList;
import java.util.Stack;

public class Tim extends AbstractSort {
    public static int index; // 위치 전역변수
    public static int minrun;
//    public static Stack<ArrayList<Comparable>> stack;
    public static Stack<Comparable[]> stack;


//    private static void make_run(Comparable[] a) {}

    public static void sort(Comparable[] a){

//        while(index < a.length){
//            ArrayList<Comparable> run = new ArrayList<>();
//            run.add(a[index++]);
//            run.add(a[index++]);
//            while(index < a.length){
//                if(less(run.get(0), run.get(1))){
//                    if(less(a[index-1], a[index])) run.add(a[index++]);
//                    else if(run.size() < minrun){
//                        run.add(a[index++]);
//                        Insertion.sort(run);
//                    }
//                    else break;
//                }
//                else{
//                    if(!less(a[index-1], a[index])) run.add(a[index++]);
//                    else if(run.size() < minrun){
//                        run.add(a[index++]);
//                        Insertion.sort_reverse(run);
//                    }
//                    else break;
//                }
//            }
//            stack.push(run);
//
//        }

        while(index < a.length){
            Comparable[] run = new Comparable[a.length];
            int run_len = 0; // 의미없는 초기값
            run[0] = a[index++];
            run[1] = a[index++];
            for(int i = 2; i < a.length; i++){ // 여기서 i가 run의 인덱스
                if(less(run[0],run[1])){
                    if(less(a[index-1], a[index])) run[i] = a[index++];
                    else if(i-1 < minrun){ //minrun의 기준보다 작은지
                        run[i] = a[index++];
                        Insertion.sort(run);
                    }
                    else{
                        run_len = i;
                        break;
                    }
                }
                else{
                    if(!less(a[index-1], a[index])) run[i] = a[index++];
                    else if(i-1 < minrun){
                        run[i] = a[index++];
                        Insertion.sort_reverse(run); //다만 내림차순 기준
                    }
                    else{
                        run_len = i;
                        // 오름차순으로 뒤집는 로직
                        break;
                    }
                }
            }
            Comparable[] temp = new Comparable[run_len];
            for(int i = 0; i < run_len; i++) temp[i] = run[i];
            stack.push(temp);


            if(stack.size() >= 3){
                Comparable[] A = stack.pop();
                Comparable[] B = stack.pop();
                Comparable[] C = stack.pop();
                if(!(C.length > A.length + B.length)){
                    if(C.length > A.length) stack.push(Merge.sort(A,B));
                    else stack.push(Merge.sort(B,C));
                }
            }
            else if(stack.size() == 2){
                Comparable[] A = stack.pop();
                Comparable[] B = stack.pop();
                if(!(B.length > A.length)){
                    stack.push(Merge.sort(A, B));
                }
            }
        }

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
