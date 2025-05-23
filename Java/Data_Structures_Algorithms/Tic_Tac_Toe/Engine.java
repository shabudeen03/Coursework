import java.util.Scanner;

public class Engine {
    private Space[][] board;
    private Scanner scan = new Scanner(System.in);
    private String[] signs = {"o", "x"};

    public static void main(String[] args) {
        Engine game = new Engine();
        game.start();
    }

    public void start() {
        System.out.println("Welcome to Tic-Tac-Toe!");

        System.out.print("\nWhat is your name? ");
        String name = scan.nextLine();
        
        System.out.print("\nWhat board size would you like to play on? (Traditional Size is 3x3, If you want it, Enter \"3\"): ");
        int value = Integer.parseInt(scan.nextLine());
        while (value < 1) {
            System.out.println("Please Enter a valid size: ");
            value = Integer.parseInt(scan.nextLine());
        }
        
        createBoard(value);

        System.out.println("\nEnter Your Player Sign (ex: X, O): ");
        String playerSign = scan.nextLine();
        String compSign = "";

        while (playerSign.length() > 1 && (playerSign.equals(" ") || playerSign.length() == 0)) {
            System.out.print("Invalid Sign. ");
            if(playerSign.length() > 1) System.out.println("Player Sign can only be one character, not several.");
            else System.out.println("You must select a character AND it cannot be \" \"!");
            System.out.println("Enter Your Player Sign (ex: X, O): ");
            playerSign = scan.nextLine();
        }

        for(String standardSign:signs) {
            if(! playerSign.toLowerCase().equals(standardSign)) {
                compSign = standardSign.toUpperCase();
                playerSign = playerSign.toUpperCase();
                break;
            }
        }
        
        String input = "";
        boolean gameOver = false;
        Player user = new Player(playerSign);

        while(! input.toLowerCase().equals("x") || gameOver) {
            System.out.println(name + "'s Sign: " + playerSign + "\nComputer Sign: " + compSign);
            System.out.println("\nHere is a " + value + "x" + value + " board:\n");
    
            drawBoard();

            System.out.print("What is your next move?\nRules: Enter Row, then Column. So row 2, column 1 would be \"21\". Has to be valid move and spot not already taken.\nMove: ");
            input = scan.nextLine();

            if(input.toLowerCase().equals("x")) {
                break;
            }

            while(input.length() != 2) {
                System.out.print("\nPlease Enter Valid Move Sequence: ");
                input = scan.nextLine();
            }

            int row = Integer.parseInt(input.substring(0, 1)) - 1;
            int col = Integer.parseInt(input.substring(1, 2)) - 1;

            while (! ((row >= 0 && row < value) && (col >= 0 && col < value))) {
                System.out.print("\nPlease Enter Valid Row & Column.\nMove: ");
                input = scan.nextLine();

                row = Integer.parseInt(input.substring(0, 1)) - 1;
                col = Integer.parseInt(input.substring(1, 2)) - 1;
            }

            Space space = board[row][col].getSpace();
            boolean validMove = space.setPlayer(user);

            if(validMove) {
                System.out.println("Congratulations. Your move was successfully conducted!");
            }
        }
    }

    public void createBoard(int size) {
        board = new Space[size][size];

        for(int i=0; i<size; i++) {
            for(int j=0; j<size; j++) {
                board[i][j] = new Space(" ");
            }
        }
    }

    public void drawBoard() {
        for(int i=0; i<board.length; i++) {
            for(int j=0; j<board[i].length - 1; j++) {
                System.out.print(" " + board[i][j] + " |");
            }

            System.out.println();

            if(i != board.length - 1) {
                for(int n=0; n<board.length; n++) {
                    System.out.print("---");

                    if(n != board.length - 1) {
                        System.out.print("+");
                    }
                }

                System.out.println();
            }
        }

        System.out.println();
    }
}