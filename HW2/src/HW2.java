import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
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
        if(cmp == 0){
            node.val = val;
        }
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
        if (key.equals(x.key))
            return x.val;
        else
            return null;
    }

    protected Node<K,V> treeSearch(K key) {
        Node<K,V> node = root;
        while (true) {
            int cmp = key.compareTo(node.key);
            if (cmp == 0) return node;
            else if (cmp < 0) {
                if (node.left == null) return node;
                else node = node.left;
            }
            else {
                if (node.right == null) return node;
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

    public int size(){ return (root != null) ? root.N : 0; }

    public Iterable<K> keys() {
        if (root == null) return null;
        ArrayList<K> keyList = new ArrayList<K>(size());
        inorder(root, keyList);
        return keyList;
    }

    private void inorder(Node<K,V> x, ArrayList<K> keyList) {
        if (x != null) {
            inorder(x.left, keyList);
            keyList.add(x.key);
            inorder(x.right, keyList);
        }
    }
}

public class HW2 {
    public static void main(String[] args) {
        Scanner sc = new Scanner(System.in);
        Scanner fsc;

        BST<String,Integer> A = new BST<>();
        BST<String,Integer> B = new BST<>();

        String shingle;

        System.out.print("첫번째 파일이름 : ");
        String aName = sc.next();
        System.out.print("두번째 파일이름 : ");
        String bName = sc.next();

        try{
            fsc = new Scanner(new File(aName));
            String content = "";
            while(fsc.hasNextLine()){
                content += fsc.nextLine() + "\n";
            }
            StringTokenizer st = new StringTokenizer(content, " \t\n=;,<>()");

            int count = 0;
            String [] buffer = new String[5];
            while(st.hasMoreTokens()){
                if(count < 4){
                    buffer[count++] = st.nextToken();
                }
                else{
                    buffer[4] = st.nextToken();
                    shingle = "";
                    for(int i=0; i<4; i++){
                        shingle += buffer[i] + " ";
                        buffer[i] = buffer[i+1];
                    }
                    shingle += buffer[4];
                    if(A.get(shingle) == null) A.put(shingle, 1);
                    else A.put(shingle, A.get(shingle) + 1);
                }
            }
            System.out.println("파일 " + aName + "의 Shingle의 수 : " + A.size());

            fsc = new Scanner(new File(bName));
            content = "";
            while(fsc.hasNextLine()){
                content += fsc.nextLine() + "\n";
            }
            st = new StringTokenizer(content, " \t\n=;,<>()");

            count = 0;
            while(st.hasMoreTokens()){
                if(count < 4){
                    buffer[count++] = st.nextToken();
                }
                else{
                    buffer[4] = st.nextToken();
                    shingle = "";
                    for(int i=0; i<4; i++){
                        shingle += buffer[i] + " ";
                        buffer[i] = buffer[i+1];
                    }
                    shingle += buffer[4];
                    if(B.get(shingle) == null) B.put(shingle, 1);
                    else B.put(shingle, B.get(shingle) + 1);
                }
            }
            System.out.println("파일 " + bName + "의 Shingle의 수 : " + B.size());

            Iterable<String> bKeys = B.keys();
            int unionSum = 0;
            int intersectionSum = 0;
            int intersectionCount = 0;

            for(String bKey : bKeys){
                if(A.get(bKey) != null){
                    intersectionCount++;
                    if(A.get(bKey) < B.get(bKey)) {
                        intersectionSum += A.get(bKey);
                        A.put(bKey, B.get(bKey));
                    }
                    else
                        intersectionSum += B.get(bKey);
                }
                else
                    A.put(bKey, B.get(bKey));
            }
            Iterable<String> unionKeys = A.keys();
            for(String unionKey : unionKeys){ unionSum += A.get(unionKey); }
            double similarity = (double) intersectionSum / unionSum;

            System.out.println("두 파일에서 공통된 Shingle의 수 : " + intersectionCount);
            System.out.println(aName + "과 " + bName + "의 유사도 : " + similarity);

        } catch (IOException e) { System.out.println(e); return; }
        if (sc != null) sc.close();
    }
}
