public class Merge extends AbstractSort {

    public static Comparable[] sort(Comparable[] a, Comparable[] b) {

        Comparable[] d = new Comparable[a.length + b.length];
        for(int i = 0; i < a.length; i++) d[i] = a[i];
        for(int i = a.length; i < d.length; i++) d[i] = b[i-a.length];

        int lo = 0; // a배열의 시작지점
        int hi = d.length - 1; // 병합된 배열의 마지막 index
        int mid = a.length; // b배열의 시작지점

        Comparable[] temp = new Comparable[d.length]; //임시로 옮겨담을 배열


        // a와 b는 모두 오름차순 정렬됨
        for (int k = lo; k <= hi; k++)
            temp[k] = d[k]; // temp에 d내용을 모두 복사

        // temp 배열을 비교하여 그 결과를 d에다가 반영
        int i = lo, j = mid;
        for (int k = lo; k <= hi; k++) {
            if (i >= mid) d[k] = temp[j++]; // 앞 배열이 초과되면 뒷배열의 원소를 모두 넣어줌
            else if (j > hi) d[k] = temp[i++];// ㅇ
            else if (less(temp[j], temp[i])) d[k] = temp[j++]; // 크기 비교
            else d[k] = temp[i++];
        }

        return d; // 정렬이 반영된 d를 반환
    }

}
