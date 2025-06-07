import java.util.regex.*;
import java.util.*;

public class HW {
    public int solution(String dartResult) {
        String regex = "((?:[0-9]|10))([SDT])([*#]?)";
        Pattern p = Pattern.compile("^" + regex + regex + regex + "$");
        Matcher m = p.matcher(dartResult);

        int[] scores = new int[3];

        int groupIndex = 1;

        if(!m.matches()) throw new IllegalArgumentException("잘못된 입력");
        for(int i=0; i<3; i++){
            int num = Integer.parseInt(m.group(groupIndex++));
            char bonus = m.group(groupIndex++).charAt(0);
            String option = m.group(groupIndex++);


            switch(bonus){
                case 'S':
                    scores[i] = num;
                    break;
                case 'D':
                    scores[i] = num * num;
                    break;
                case 'T':
                    scores[i] = num * num * num;
                    break;
                default:
                    throw new IllegalArgumentException("잘못된 입력");
            }

            if("*".equals(option)){
                scores[i] *= 2;
                if(i!=0) scores[i-1] *= 2;
            }
            if("#".equals(option)){
                scores[i] *= -1;
            }

        }


        return scores[0] + scores[1] + scores[2];
    }
}