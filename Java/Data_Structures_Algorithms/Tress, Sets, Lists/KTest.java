package HW4;
import java.util.Scanner;
public class KTest {
 public static void main(String[] args) {
  AVLTree tree = new AVLTree<>();
  Scanner input = new Scanner(System.in);
  System.out.print("Enter 15 numbers: ");
  for (int i = 0; i < 15; i++) {
   tree.insert(input.nextDouble());
 }
 
 for(int i=1; i<=15; i++) {
    System.out.println(tree.find(i));
 }
 }
}
