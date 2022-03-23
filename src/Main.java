public class Main {

    /**
     * Main class handling the game
     *
     * @author August Johnson Palm
     */

    private static final int HUMAN_PLAYER = 1;
    private static final int COMPUTER = 2;

    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    private BoardHandler boardHandler;
    private Input scanner;
    private int difficulty;

    public static void main(String[] args) throws MoveException, PlayerException {
        Main game = new Main();
        game.run();
    }

    private void run() throws MoveException, PlayerException {
        initialize();
        runLoop();
        exit();
    }

    private void initialize(){
        boardHandler = new BoardHandler(HUMAN_PLAYER, COMPUTER);
        scanner = new Input();
        chooseDifficulty();
    }

    private void chooseDifficulty(){
        System.out.println("------ OTHELLO -------\n");
        System.out.println("Please choose difficulty level:");
        System.out.println("Easy: 1");
        System.out.println("Medium: 2");
        System.out.println("Hard: 1\n");
        System.out.print("Difficulty: ");

        int choice = scanner.nextInt();
        while (!isValidLevel(choice)){
            System.out.println("Not a valid level! Try again!\n");
            System.out.print("Difficulty: ");
            choice = scanner.nextInt();
        }

        difficulty = choice;
    }



    private void runLoop() throws PlayerException, MoveException {
        boolean gameOver = false;
        AI ai = new AI(boardHandler, difficulty, HUMAN_PLAYER, COMPUTER);

        Move nextMove;
        printScore();
        while (!gameOver) {
            int currentPlayer = boardHandler.hasTurn(HUMAN_PLAYER) ? HUMAN_PLAYER : COMPUTER;

            if (boardHandler.hasTurn(HUMAN_PLAYER)) {
                System.out.print("Enter row: ");
                int i = scanner.nextInt();
                System.out.print("Enter column: ");
                int j = scanner.nextColumn();
                System.out.println(i + " " + j);
                nextMove = new Move(i-1, j);
            } else {
                ai.setNextMove();
                nextMove = ai.getNextMove();
            }


            if (boardHandler.makeMove(nextMove, currentPlayer)) {
                printScore();
                boardHandler.changeTurn();
                gameOver = boardHandler.gameOver();
            }
            else
                System.out.println("Unvalid cell");
        }

        if (hasWon(COMPUTER))
            System.out.println("You lose");
        else if (hasWon(HUMAN_PLAYER))
            System.out.println("You won");
        else
            System.out.println("Draw");

        printScore();
    }

    private void exit(){
        scanner.close();
    }


    private boolean isValidLevel(int n){
        return n == EASY || n == MEDIUM || n == HARD;
    }

    private boolean hasWon(int player) throws PlayerException {
        int opponent = player == HUMAN_PLAYER ? COMPUTER : HUMAN_PLAYER;
        return boardHandler.getPoints(player) > boardHandler.getPoints(opponent);
    }

    private void printScore() throws PlayerException {
        System.out.println("Your score: " + boardHandler.getPoints(HUMAN_PLAYER));
        System.out.println("Computer score: " + boardHandler.getPoints(COMPUTER));
        System.out.println(boardHandler.toString() + "\n");
    }


    // for testing
    /*private void AIvsAI() throws PlayerException, MoveException {
        boolean gameOver = false;
        AI ai1 = new AI(boardHandler, 3, 2, 1);
        AI ai2 = new AI(boardHandler, 2, 1, 2);

        Move nextMove;
        printScore();

        while (!gameOver) {
            int currentPlayer = boardHandler.hasTurn(1) ? 1 : 2;

            if (boardHandler.hasTurn(1)) {
                ai1.setNextMove();
                nextMove = ai1.getNextMove();
                if (nextMove != null)
                    System.out.print("AI1 choose cell: " + nextMove.row() + " " + nextMove.column());
                else System.out.println("AI1 has no move");
            } else {
                ai2.setNextMove();
                nextMove = ai2.getNextMove();
                if (nextMove != null)
                    System.out.print("AI2 choose cell: " + nextMove.row() + " " + nextMove.column());
                else System.out.println("AI2 has no move");
            }
            System.out.println();


            if (nextMove != null && boardHandler.makeMove(nextMove, currentPlayer)) {
                printScore();
            }
            boardHandler.changeTurn();
            gameOver = boardHandler.gameOver();
        }

        if (boardHandler.getPoints(2) > boardHandler.getPoints(1))
            System.out.println("AI2 won");
        else if (boardHandler.getPoints(2) < boardHandler.getPoints(1))
            System.out.println("AI1 won");
        else
            System.out.println("Draw");
        System.out.print("AI1 score: " + boardHandler.getPoints(1) + "\n");
        System.out.print("AI2 score: " + boardHandler.getPoints(2) + "\n");
        System.out.println(boardHandler.toString());
    }
     */
}


