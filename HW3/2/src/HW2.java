// 22112155 최정
import java.util.Scanner;

public class HW2 {
    public static int[] set;
    public static int N = 0;

    public static void subset(int n, int k, int r){
        for(int i=r; i<=n; i++){
            set[N++] = i;
            if(N==k){
                System.out.print("[");
                for(int j=0; j<k-1; j++) System.out.print(set[j] + ", ");
                System.out.print(set[k-1] + "] ");
            }
            else
                subset(n, k, i+1);
            N--;
        }
    }

    public static void main(String[] args){
        Scanner sc = new Scanner(System.in);

        System.out.print("정수 n과 k를 입력해주세요 : ");
        int n = sc.nextInt();
        int k = sc.nextInt();

        set = new int[k];
        subset(n, k, 1);
    }
}
