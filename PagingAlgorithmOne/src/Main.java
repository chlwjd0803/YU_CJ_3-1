import javax.swing.*;
import java.util.Arrays;
import java.util.Scanner;

public class Main {
    public static void main(String[] args){

        SwingUtilities.invokeLater(PageReplacementUI::new);

//        Scanner sc = new Scanner(System.in);
//        System.out.print("frame의 용량을 입력하세요 : ");
//        int frameCapacity = sc.nextInt(); // 프레임의 용량
//        System.out.print("Reference String 길이를 입력해주세요 : ");
//        int refStrLen = sc.nextInt(); // 페이지번호 배열의 길이
//        System.out.print("Reference String에 들어갈 페이지 범위를 입력해주세요. (1~n) : ");
//        int scope = sc.nextInt(); // 나올 페이지 번호의 범위

//        int [] refStr = new int[refStrLen]; // 페이지번호 배열 선언만
//        int [] refStr = {2, 3, 4, 3, 4, 3, 5, 5, 2, 2, 3, 1};

//        for(int i=0; i<refStrLen; i++)
//            refStr[i] = (int)(Math.random() * scope) + 1;

//        System.out.println("불러올 페이지들 : " + Arrays.toString(refStr));

//        FIFO.algorithm(frameCapacity, refStr);
//        Optimal.algorithm(frameCapacity, refStr);
//        LRU.algorithm(frameCapacity, refStr);
//        sc.close();
    }
}
