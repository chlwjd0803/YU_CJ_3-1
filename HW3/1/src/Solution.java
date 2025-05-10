// 22112155 최정
public class Solution {

    private void compact(int[] answer, int[][] arr, int row, int col, int N){
        int init = arr[row][col];
        boolean flag = true;

        if(N==1){
            answer[init]++;
            return;
        }

        for(int i=row; i<row+N && flag; i++){
            for(int j=col; j<col+N; j++){
                if(init != arr[i][j]){
                    flag = false;
                    break;
                }
            }
        }
        if(flag){
            answer[init]++;
        }
        else{
            compact(answer, arr, row, col, N/2);
            compact(answer, arr, row+N/2, col, N/2);
            compact(answer, arr, row, col+N/2, N/2);
            compact(answer, arr, row+N/2, col+N/2, N/2);
        }
    }


    public int[] solution(int[][] arr){
        int[] answer = {0, 0};
        compact(answer, arr, 0, 0, arr.length);
        return answer;
    }
}
