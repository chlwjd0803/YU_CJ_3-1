import java.util.Arrays;

public class Solution {
    public int solution(int[][] routes){
        int camera = 0;
        int len = routes.length;
        Arrays.sort(routes, (a, b) -> Integer.compare(a[0], b[0]));
        int i = 0;
        int j = 1;

        while(i+j < len){
            if(routes[i][0] <= routes[i+j][0] && routes[i][1] >= routes[i+j][0]){
                j++;
                if(i+j == len){
                    camera++;
                    break;
                }
            }
            else{
                i += j;
                j=1;
                camera++;
            }
        }
        return camera;
    }

    public static void main(String[] args) {
        Solution solution = new Solution();
        System.out.println(solution.solution(new int[][]{{-20,-15}, {-14,-5}, {-18,-13}, {-5,-3}}));
    }
}
