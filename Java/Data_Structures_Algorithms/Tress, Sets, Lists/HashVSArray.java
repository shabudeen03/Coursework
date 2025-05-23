package HW4;
import java.util.*;

public class HashVSArray {
    private static ArrayList<Integer> adding = new ArrayList<Integer>();
    private static ArrayList<Integer> testing = new ArrayList<Integer>();
    private static int num = 1000000; //1000000

    public static void main(String[] args) {
        for(int i=0; i<num; i++) {
            adding.add((int) (Math.random() * num));
            testing.add((int) (Math.random() * num));
        }

        System.out.println("MyHashSet Time: " + testMySet());
        System.out.println("MyArraylist Time: " + testMyList());
    }

    public static long testMyList() {
        long start = System.currentTimeMillis();

        MyArrayList<Integer> myList = new MyArrayList<Integer>();
        for(int i=0; i<num; i++) {
            myList.add(adding.get(i));
        }

        for(int i=0; i<num; i++) {
            if(myList.contains(testing.get(i))) {
                continue;
            }
        }

        long end = System.currentTimeMillis();
        return end - start;
    }

    public static long testMySet() {
        long start = System.currentTimeMillis();

        MyHashSet<Integer> set = new MyHashSet<Integer>();

        for(int i=0; i<num; i++) {
            set.add(adding.get(i));
        }

        for(int i=0; i<num; i++) {
            if(set.contains(testing.get(i))) {
                continue;
            }
        }

        long end = System.currentTimeMillis();
        return end - start;
    }
}
