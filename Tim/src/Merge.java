public class Merge extends AbstractSort {

    public static Comparable[] sort(Comparable[] a, Comparable[] b) {

        Comparable[] d = new Comparable[a.length + b.length];
        for(int i = 0; i < a.length; i++) d[i] = a[i];
        for(int i = a.length; i < d.length; i++) d[i] = b[i];

        int lo = 0;
        int hi = a.length - 1;
        int mid = lo + (hi - lo) / 2;

        Comparable[] temp = new Comparable[d.length];


        // a[lo .. mid] and a[mid+1 .. hi] 는 이미 정렬
        for (int k = lo; k <= hi; k++)
            temp[k] = d[k]; // aux[] 배열에 a[]의 내용을 일단 복사

        // aux[] 배열을 비교하여 병합된 결과를 a[] 배열에 다시 저장
        int i = lo, j = mid+1;
        for (int k = lo; k <= hi; k++) {
            if (i > mid) d[k] = temp[j++];
            else if (j > hi) d[k] = temp[i++];
            else if (less(temp[j], temp[i])) d[k] = temp[j++];
            else d[k] = temp[i++];
        }

        return d;
    }

}
