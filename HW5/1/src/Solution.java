// 22112155 최정
import java.util.Scanner;

public class Solution {
    public String solution(String number, int endIndex){
        StringBuilder answer = new StringBuilder();
        int maxIndex;
        int startIndex = 0;

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
}
