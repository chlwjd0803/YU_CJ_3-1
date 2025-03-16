public class Insertion extends AbstractSort {

    public static void sort(Comparable[] a, int len) {
        for (int i = len; i > 0; i--) if (less(a[i], a[i - 1])) exch(a, i, i - 1);
    }

    public static void sort_reverse(Comparable[] a, int len) {
        for(int i = len; i > 0; i--) if (less(a[i-1], a[i])) exch(a, i, i - 1);
    }

}