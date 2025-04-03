// 22112155 최정
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

    public static void adjust(Distance[] d, int parent, int n){
        int child = 2*parent;

        while(child <= n){
            if(child < n && less(d[child+1].dist, d[child].dist)) child++;
            if(less(d[child].dist, d[parent].dist)){
                exch(d,parent,child);
                parent = child;
                child *= 2;
            }
            else break;
        }
    }

    public static void sort(Distance[] d, int k, int n, boolean enhanced){
        for(int i=n/2; i>0; i--) adjust(d, i, n);

        int count = 0;
        for(int i=n-1; i>0; i--){
            exch(d, 1, i+1);
            if(count++ == k && enhanced) break;
            adjust(d, 1, i);
        }
    }
}

class Distance{
    public double x;
    public double y;
    public double dist;

    public Distance(double cur_x, double cur_y, double ins_x, double ins_y){
        this.x = ins_x;
        this.y = ins_y;
        this.dist = Math.sqrt(Math.pow(cur_x-ins_x, 2) + Math.pow(cur_y-ins_y, 2));
    }
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
            Distance[] d1 = new Distance[n+1];
            Distance[] d2 = new Distance[n+1];
            double ins_x, ins_y;
            System.out.println("데이터 입력 중");
            for(int i=1; i<=n; i++){
                ins_x = sc.nextDouble();
                ins_y = sc.nextDouble();
                d1[i] = new Distance(x, y, ins_x, ins_y);
                d2[i] = new Distance(x, y, ins_x, ins_y);
            }

            System.out.println("데이터 입력 완료\n\n\n");
            System.out.println("기본적인 정렬 시작");
            long start = System.currentTimeMillis();
            Heap.sort(d1, k, n, false);
            long end = System.currentTimeMillis();
            System.out.println("k="+ k + " 일때 " + "실행 시간 (밀리초): " + (end-start) + "ms");
            for(int i=n; i>n-k; i--) System.out.println(n-i + ": (" + d1[i].x + ", " + d1[i].y + ") 거리 = " + d1[i].dist);
            System.out.println("\n\n\n-----------------------------------------------------\n\n\n");

            System.out.println("개선된 정렬 시작");
            start = System.currentTimeMillis();
            Heap.sort(d2, k, n, true);
            end = System.currentTimeMillis();
            System.out.println("k="+ k + " 일때 " + "실행 시간 (밀리초): " + (end-start) + "ms");
            for(int i=n; i>n-k; i--) System.out.println(n-i + ": (" + d2[i].x + ", " + d2[i].y + ") 거리 = " + d2[i].dist);
            System.out.println("\n종료합니다.");

        } catch (IOException e) { System.out.println(e); return; }
        if (sc != null) sc.close();
    }
}