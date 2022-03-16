import java.util.Scanner;

public class Main {

    public static void userVsAI(BoardHandler bh, int difficulty) {
        boolean gameOver = false;
        Scanner scanner = new Scanner(System.in);
        AI ai = new AI(bh, difficulty, 1, 2);

        Move nextMove;
        System.out.println(bh);
        System.out.println();

        while (!gameOver) {
            int currentPlayer = bh.hasTurn(1) ? 1 : 2;

            if (bh.hasTurn(1)) {
                System.out.print("Enter i: ");
                int x = scanner.nextInt();
                System.out.print("Enter j: ");
                int y = scanner.nextInt();
                System.out.println();
                nextMove = new Move(x-1, y-1);
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

    public static void AIvsAI(BoardHandler bh) {
        boolean gameOver = false;
        AI ai1 = new AI(bh, 3, 2, 1);
        AI ai2 = new AI(bh, 2, 1, 2);

        Move nextMove;
        System.out.println(bh);
        System.out.println();

        Stopwatch stopwatch = new Stopwatch();
        stopwatch.start();
        while (!gameOver) {
            int currentPlayer = bh.hasTurn(1) ? 1 : 2;

            if (bh.hasTurn(1)) {
                ai1.setNextMove();
                nextMove = ai1.getNextMove();
                if (nextMove != null)
                    System.out.print("AI1 choose cell: " + nextMove.x() + " " + nextMove.y());
                else System.out.println("AI1 has no move");
            } else {
                ai2.setNextMove();
                nextMove = ai2.getNextMove();
                if (nextMove != null)
                    System.out.print("AI2 choose cell: " + nextMove.x() + " " + nextMove.y());
                else System.out.println("AI2 has no move");
            }
            System.out.println();


            if (nextMove != null && bh.makeMove(nextMove.x(), nextMove.y(), currentPlayer)) {
                System.out.println(bh.toString());
                System.out.print("AI1 score: " + bh.getPoints(1));
                System.out.println();
                System.out.print("AI2 score: " + bh.getPoints(2));
                System.out.println();
            }
            bh.changeTurn();
            gameOver = bh.gameOver();
        }

        if (bh.getPoints(2) > bh.getPoints(1))
            System.out.println("AI2 won");
        else if (bh.getPoints(2) < bh.getPoints(1))
            System.out.println("AI1 won");
        else
            System.out.println("Draw");
        System.out.println(bh.toString());
        stopwatch.stop();

        System.out.println(stopwatch);
    }


    public static void main(String[] args) {
        BoardHandler bh = new BoardHandler(1, 2);

        /*test.makeMove(3, 2, AI);
        System.out.println(test);
        test.makeMove(2, 4, HUMAN);
        System.out.println(test);
        test.makeMove(5, 5, AI);
        System.out.println(test);
        System.out.println(test.getPoints(AI));
        System.out.println(test.getPoints(HUMAN));
        System.out.println(test.getTotalDiscs());*/


       // testGame(bh, 3);

        AIvsAI(bh);

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


