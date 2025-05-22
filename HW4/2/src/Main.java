// 22112155 최정
import java.util.Scanner;

public class Main {
    public static String solution(String s1, String s2) {
        int l1 = s1.length();
        int l2 = s2.length();
        int[][] lcs = new int[l1+1][l2+1];
        int totalLen = 0;
        char[] buf = new char[(l1>l2) ? l1+1 : l2+1];


        for (int i = 0; i <= l1; i++) {
            lcs[i][0] = 0;
        }

        for (int j = 0; j <= l2; j++) {
            lcs[0][j] = 0;
        }


        for(int i=1; i<=l1; i++) for(int j=1; j<=l2; j++){
            if(s1.charAt(i-1) == s2.charAt(j-1)){
                lcs[i][j] = lcs[i-1][j-1] + 1;
                if(totalLen < lcs[i][j]){
                    buf[totalLen] = s1.charAt(i-1);
                    totalLen = lcs[i][j];
                }
            }
            else
                lcs[i][j] = Math.max(lcs[i-1][j], lcs[i][j-1]);
        }

        char[] answer = new char[totalLen];
        System.arraycopy(buf, 0, answer, 0, totalLen);
        return new String(answer);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        String s1 = scanner.nextLine();
        String s2 = scanner.nextLine();

        String answer = solution(s1, s2);
        System.out.print(answer + "\n" + answer.length());
    }
}