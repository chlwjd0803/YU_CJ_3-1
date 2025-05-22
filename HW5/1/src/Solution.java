import java.util.Scanner;

public class Solution {

    public String solution(String number, int k){
        StringBuilder answer = new StringBuilder();
        int maxIndex; // 임시로..
        int startIndex = 0;
        int endIndex = k;

        while(endIndex < number.length()){
            maxIndex = startIndex;
            for(int i=startIndex+1; i<=endIndex; i++){
                if(number.charAt(i) > number.charAt(maxIndex))
                    maxIndex = i;

            }
            answer.append(number.charAt(maxIndex));
            endIndex++;
            startIndex = maxIndex + 1;
        }

        return answer.toString();
    }


    public static void main(String[] args) {
        Solution solution = new Solution();
        Scanner sc = new Scanner(System.in);
        String number = sc.nextLine();
        int k = sc.nextInt();
        System.out.println(solution.solution(number, k));
    }

}
