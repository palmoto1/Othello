import java.util.Scanner;

public class Main {

    public static void testGame(BoardHandler bh, int difficulty) {
        boolean gameOver = false;
        Scanner scanner = new Scanner(System.in);
        AI ai = new AI(bh, difficulty);

        Move nextMove;

        while (!gameOver) {
            int currentPlayer = bh.hasTurn(1) ? 1 : 2;

            if (bh.hasTurn(1)) {
                System.out.print("Enter i: ");
                int x = scanner.nextInt();
                System.out.print("Enter j: ");
                int y = scanner.nextInt();
                System.out.println();
                nextMove = new Move(x, y);
            } else {
                ai.setNextMove();
                nextMove = ai.getNextMove();
                System.out.print("AI choose cell: " + nextMove.x() + " " + nextMove.y());
                System.out.println();
            }


            if (bh.makeMove(nextMove.x(), nextMove.y(), currentPlayer)) {
                System.out.println(bh.toString());
                System.out.print("Your score: " + bh.getPoints(1));
                System.out.println();
                System.out.print("AI score: " + bh.getPoints(2));
                System.out.println();
                bh.changeTurn();
                gameOver = bh.gameOver();
            }
            else
                System.out.println("Unvalid cell");
        }

        if (bh.getPoints(2) > bh.getPoints(1))
            System.out.println("You lose");
        else if (bh.getPoints(2) < bh.getPoints(1))
            System.out.println("You won");
        else
            System.out.println("Draw");
        System.out.println(bh.toString());
    }


    public static void main(String[] args) {
        BoardHandler bh = new BoardHandler();

        /*test.makeMove(3, 2, AI);
        System.out.println(test);
        test.makeMove(2, 4, HUMAN);
        System.out.println(test);
        test.makeMove(5, 5, AI);
        System.out.println(test);
        System.out.println(test.getPoints(AI));
        System.out.println(test.getPoints(HUMAN));
        System.out.println(test.getTotalDiscs());*/
        testGame(bh, 3);
    }
}

/*public static void main(String[] args) {
        BoardHandler test = new BoardHandler();
        System.out.println(test);
        test.makeMove(2, 3, AI);
        System.out.println(test);
        test.makeMove(2, 2, HUMAN);

        System.out.println(test);
    }*/


