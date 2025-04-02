import java.io.File;
import java.io.IOException;
import java.util.Scanner;

abstract class AbstractSort {
    public static void sort(Comparable[] a) { };

    protected static boolean less(Comparable v, Comparable w)
    { return v.compareTo(w) < 0; }

    protected static void exch(Comparable[] a, int i, int j)
    { Comparable t = a[i]; a[i] = a[j];a[j] = t; }

    protected static void show(Comparable[] a) {
        for (int i = 0; i < a.length; i++) System.out.print(a[i] + " ");
        System.out.println();
    }

    protected static boolean isSorted(Comparable[] a) {
        for (int i = 1; i < a.length; i++)
            if (less(a[i], a[i-1])) return false;
        return true;
    }
}


class Heap extends AbstractSort{

    protected static void exch(Distance[] a, int i, int j)
    { Distance t = a[i]; a[i] = a[j];a[j] = t; }

    public static void adjust(Distance[] d, int parent){
        int lch = 2*parent;
        int rch = lch+1;
        if(rch >= d.length);

        if(less(d[lch].dist, d[parent].dist)) exch(d, lch, parent);
        if(rch >= d.length) return;
        if(less(d[rch].dist, d[parent].dist)) exch(d, rch, parent);
        

    }

    public static void sort(Distance[] d, int k, int n){
//        Distance temp;

        for(int i=n/2; i>0; i--) adjust(d, i);
    }
}

class Distance{
    public double x;
    public double y;
    public double dist;

    public Distance(double x, double y){
        this.x = x;
        this.y = y;
        this.dist = Math.sqrt(Math.pow(x, 2) + Math.pow(y, 2));
    }

//    public toString(){};
}


public class HW1 {
    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);
        System.out.println("입력 파일 이름? ");
        String fname = sc.nextLine();
        sc.close();
        try {
            sc = new Scanner(new File(fname));
            double x = sc.nextDouble();
            double y = sc.nextDouble();
            int k = sc.nextInt();
            int n = sc.nextInt();

            Distance[] d = new Distance[n+1];
            for(int i=1; i<=n; i++)
                d[i] = new Distance(x - sc.nextDouble(), y - sc.nextDouble());
        } catch (IOException e) { System.out.println(e); return; }
        if (sc != null) sc.close();


    }
}
