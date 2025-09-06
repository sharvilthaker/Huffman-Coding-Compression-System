import java.io.*;
import java.util.*;

public class Huffman {
    //Huffman Tree Root
    private BinaryTree<Pair> huffmanTree;
    //Array for uppercase letters
    private String[] upperHuff;

    // Constructor, from the probability file
    public Huffman(String filename) throws IOException {
        List<BinaryTree<Pair>> huffTrees = buildProbTrees(filename);
        huffmanTree = buildHuffmanTree(huffTrees);
        upperHuff = findEncoding(huffmanTree);
    }

    /* this method and the logic of the method with the constructor, I have taken idea from
    https://chatgpt.com/ to not only read the file but to create initial binary trees
    for each letter probability pair which I was having difficulties to pair both.
    */
    private List<BinaryTree<Pair>> buildProbTrees(String filename) throws IOException {
        List<BinaryTree<Pair>> trees = new ArrayList<>();
        BufferedReader  typeIn = new BufferedReader(new FileReader(filename));
        String insert;

        while ((insert = typeIn.readLine()) != null) {
            String[] textLine = insert.split("\\s+");
            char UP = textLine[0].charAt(0);
            double probability = Double.parseDouble(textLine[1]);
            BinaryTree<Pair> prob = new BinaryTree<>();
            prob.makeRoot(new Pair(UP, probability));
            trees.add(prob);
        }

        typeIn.close();
        trees.sort(Comparator.comparing(t -> t.getData().getProb()));
        return trees;
    }

    // Method that builds the Huffman Tree using two queues S, T.
    private BinaryTree<Pair> buildHuffmanTree(List<BinaryTree<Pair>> trees) {
        Queue<BinaryTree<Pair>> S = new LinkedList<>(trees);
        Queue<BinaryTree<Pair>> T = new LinkedList<>();

        //Continue combining until only one remains.
        while (S.size() + T.size() > 1) {

            //Smallest subTree
            BinaryTree<Pair> A = getSubTree(S, T);
            //Second_Smallest subtree
            BinaryTree<Pair> B = getSubTree(S, T);

            //new (P)
            BinaryTree<Pair> P = new BinaryTree<>();
            P.makeRoot(new Pair('0', A.getData().getProb() + B.getData().getProb()));
            P.attachLeft(A);
            P.attachRight(B);

            // combined tree is added to T
            T.offer(P);
        }

        return T.poll();
    }

    //a helper for finding the shortest tree between the two queues
    // Taking from S if the T is empty or S smaller, otherwise take T.
    private BinaryTree<Pair> getSubTree(Queue<BinaryTree<Pair>> S, Queue<BinaryTree<Pair>> T) {
        if (T.isEmpty() || (!S.isEmpty() && S.peek().getData().getProb() < T.peek().getData().getProb())) {
            return S.poll();
        } else {
            return T.poll();
        }
    }


    private static String[] findEncoding(BinaryTree<Pair> bt) {
        String[] result = new String[26];
        findEncoding(bt, result, "");
        return result;
    }

    private static void findEncoding(BinaryTree<Pair> bt, String[] a, String prefix) {
        if (bt.getLeft() == null && bt.getRight() == null) {
            a[bt.getData().getValue() - 'A'] = prefix;
        } else {
            findEncoding(bt.getLeft(), a, prefix + "0");
            findEncoding(bt.getRight(), a, prefix + "1");
        }
    }

    // Method encode for encoding line of text with Huffman code.
    public String encode(String text) {
        StringBuilder encodeText = new StringBuilder();
        for (char letter : text.toCharArray()) {
            if (letter == ' ') {
                // to keep the spaces
                encodeText.append(" ");
            } else {
                encodeText.append(upperHuff[letter - 'A']);
            }
        }
        return encodeText.toString();
    }

    // method decode for decoding string of binary numbers into original txt.
    public String decode(String encoded) {
        StringBuilder decodedText = new StringBuilder();
        BinaryTree<Pair> user = huffmanTree;

        for (char letter : encoded.toCharArray()) {
            if (letter == ' ') {
                decodedText.append(" ");
                // Reset
                user = huffmanTree;
            } else {
                // traverse tree
                user = letter == '0' ? user.getLeft() : user.getRight();
                if (user.getLeft() == null && user.getRight() == null) {
                    decodedText.append(user.getData().getValue());
                    user = huffmanTree;
                }
            }
        }
        return decodedText.toString();
    }

    public static void main(String[] args) throws IOException {
        Scanner scanner = new Scanner(System.in);

        System.out.println("Huffman Coding");

        System.out.print("Enter the name of the file with letters and probability: ");
        String filename = scanner.nextLine();

        System.out.println("\nBuilding the Huffman tree ….");
        Huffman huffman = new Huffman(filename);
        System.out.println("Huffman coding completed.\n");

        System.out.print("Enter a line (uppercase letters only): ");
        String text = scanner.nextLine();

        String encoded = huffman.encode(text);
        System.out.println("Here’s the encoded line: " + encoded);

        String decoded = huffman.decode(encoded);
        System.out.println("The decoded line is: " + decoded);

        scanner.close();
    }

}
