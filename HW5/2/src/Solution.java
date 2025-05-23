// 22112155 최정
import java.util.Arrays;

public class Solution {
    public int solution(int[][] routes){
        int len = routes.length;
        Arrays.sort(routes, (a, b) -> Integer.compare(a[1], b[1]));
        int cameraPosition = routes[0][1];
        int camera = 1;

        for(int i = 0; i<len; i++){
            if(routes[i][0] > cameraPosition){
                camera++;
                cameraPosition = routes[i][1];
            }
        }
        return camera;
    }
}
