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

        // 둘중에 작은걸 찾고 그거를 부모와 비교
        // 두 자식인가 한 자식인가로 일단 출발?
        // 일단 두 자식이며 왼쪽보다 오른쪽이 작다면

        while(child < n){
            // 두 자식중 더 작은값을 가르키게 함, 오른쪽 자식이 존재하는지 까지 검사 (배열 오류 고려)
            if(child < n && less(d[child+1].dist, d[child].dist)) child++;
            // 자식이 작다면 부모와 교환
            if(less(d[child].dist, d[parent].dist)) exch(d,parent,child);
            else break;
        }
    }

    public static void sort(Distance[] d, int k, int n){
        // 초기 min heap 생성
        for(int i=n/2; i>0; i--) adjust(d, i, n);

        // 작업 시작
        int count = 0;
        for(int i=n-1; i>0; i--){
            exch(d, 1, n+1);
            if(count++ == k) break; //k만큼만 정렬하므로
            adjust(d, 1, i); // 루트가 바뀌었으므로 얘 기준으로만 하면 된다
        }
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

            long start = System.currentTimeMillis();
            Heap.sort(d, k, n);
            long end = System.currentTimeMillis();
            System.out.println("실행 시간 (밀리초): " + (end-start));
        } catch (IOException e) { System.out.println(e); return; }
        if (sc != null) sc.close();




    }
}
