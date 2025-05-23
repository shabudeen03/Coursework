package Hw1;

import java.util.Stack;
import java.io.File;
import java.util.Scanner;


public class MatchParens {
    public static void main(String[] args) throws Exception {
        Stack<String> stack = new Stack<String>();
        File file = new File(args[0]);
        Scanner input = new Scanner(file);
        
        while(input.hasNextLine()) {
            String line = input.nextLine();

            for(int i=0; i<line.length(); i++) {
                String s = line.substring(i, i+1);

                if(s.equals("(") || s.equals("[") || s.equals("{")) {
                    stack.push(s);
                } else if(s.equals(")")) {
                    if(stack.peek().equals("(")) {
                        stack.pop();
                    } else {
                        break;
                    }
                } else if(s.equals("]")) {
                    if(stack.peek().equals("[")) {
                        stack.pop();
                    } else {
                        break;
                    }
                } else if(s.equals("}")) {
                    if(stack.peek().equals("{")) {
                        stack.pop();
                    } else {
                        break;
                    }
                } 
                
            }
        }

        input.close();

        if(stack.empty()) {
            System.out.println("Correct");
        } else {
            System.out.println("Incorrect");
        }
    }
}
