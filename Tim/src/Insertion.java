import java.util.ArrayList;

public class Insertion extends AbstractSort {

    // ArrayList용 오름차순 삽입정렬
    public static void sort(ArrayList<Comparable> list) {
        int N = list.size();
        for (int i = 1; i < N; i++) {
            for (int j = i; j > 0 && less(list.get(j), list.get(j-1)); j--) {
                exch(list, j, j-1);
            }
        }
    }

    // ArrayList용 내림차순 삽입정렬
    public static void sort_reverse(ArrayList<Comparable> list) {
        int N = list.size();
        // 오름차순과 유사하게 진행하되, 비교 조건 반대로 설정
        for (int i = 1; i < N; i++) {
            for (int j = i; j > 0 && list.get(j).compareTo(list.get(j-1)) > 0; j--) {
                exch(list, j, j-1);
            }
        }
    }
    // ArrayList 전용 교환 메서드
    private static void exch(ArrayList<Comparable> list, int i, int j) {
        Comparable temp = list.get(i);
        list.set(i, list.get(j));
        list.set(j, temp);
    }
}
