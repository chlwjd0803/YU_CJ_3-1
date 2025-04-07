import java.io.File;
import java.io.IOException;
import java.util.Scanner;
import java.util.StringTokenizer;

class Node<K,V> {
    K key;
    V val;
    Integer N;
    Node<K,V> parent;
    Node<K,V> left;
    Node<K,V> right;
    public Node(K key, V val) {
        this.key = key;
        this.val = val;
        this.N = 1;
        parent = null;
        left = null;
        right = null;
    }
}

class BST<K extends Comparable<K>,V> {
    protected Node<K,V> root;

    public void put(K key, V val) {
        if (root == null) {
            root = new Node<K,V>(key, val);
            return;
        }
        Node<K,V> node = treeSearch(key);
        int cmp = key.compareTo(node.key);
        if(cmp == 0) node.val = val;
        else{
            var newNode = new Node<K,V>(key, val);
            if(cmp < 0) node.left = newNode;
            else node.right = newNode;
            newNode.parent = node;
            rebalanceInsert(newNode);
        }
    }

    public V get(K key) {
        if (root == null) return null;
        Node<K,V> x = treeSearch(key);
        if (key.equals(x.key)) // 검색 키를 가진 노드가 반환된 경우
            return x.val;
        else // 검색 키를 가진 노드가 없는 경우
            return null;
    }

    protected Node<K,V> treeSearch(K key) {
        Node<K,V> node = root; // BST에 대한 모든 연산은 루트부터 시작
        while (true) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0) return node; // 찾았으면, 순회 종료
            else if (cmp < 0) { // x.key보다 작을 경우, 왼쪽으로
                if (node.left == null) return node; // 없으면, 순회 종료
                else node = node.left;
            }
            else { // x.key보다 클 경우, 오른쪽으로
                if (node.right == null) return node; // 없으면, 순회 종료
                else node = node.right;
            }
        }
    }

    private void resetSize(Node<K,V> node, int val){
        for(; node != null; node = node.parent) node.N += val;
    }

    protected void rebalanceInsert(Node<K,V> node) {
        resetSize(node.parent, 1);
    }

}

public class Main {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner fsc;

        BST<String,Integer> fBst = new BST<>();
        BST<String,Integer> sBst = new BST<>();

        String shingle;

        System.out.print("첫번째 파일이름 : ");
        String fName = sc.nextLine();
        System.out.print("두번째 파일이름 : ");
        String SName = sc.nextLine();

        try{
            fsc = new Scanner(new File(fName));
            String content = "";
            while(fsc.hasNextLine()){
                content += fsc.nextLine() + "\n";
            }
            StringTokenizer st = new StringTokenizer(content);

            int count = 0;
            String [] buffer = new String[5];
            while(st.hasMoreTokens()){
                if(count < 4){
                    buffer[count++] = st.nextToken();
                }
                else{
                    buffer[4] = st.nextToken();
                    shingle = "";
                    for(int i=0; i<5; i++){
                        shingle += buffer[i];
                        buffer[i] = buffer[i+1];
                    }
                    shingle += buffer[4];
                    if(fBst.get(shingle) == null) fBst.put(shingle, 1);
                    else fBst.put(shingle, fBst.get(shingle) + 1);
                }

            }

            fsc = new Scanner(new File(SName));
            content = "";
            while(fsc.hasNextLine()){
                content += fsc.nextLine() + "\n";
            }
            st = new StringTokenizer(content);

            count = 0;
            while(st.hasMoreTokens()){
                if(count < 4){
                    buffer[count++] = st.nextToken();
                }
                else{
                    buffer[4] = st.nextToken();
                    shingle = "";
                    for(int i=0; i<4; i++){
                        shingle += buffer[i];
                        buffer[i] = buffer[i+1];
                    }
                    shingle += buffer[4];
                    if(sBst.get(shingle) == null) sBst.put(shingle, 1);
                    else sBst.put(shingle, sBst.get(shingle) + 1);
                }
            }

        } catch (IOException e) { System.out.println(e); return; }
        if (sc != null) sc.close();



        StringTokenizer st = new StringTokenizer(args[0], " \t\n=;,<>()");
    }
}
