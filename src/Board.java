import java.util.*;

public class Board {

    private final static int SIZE = 8;
    private final static int TOTAL_CELLS = 64;
    private final static int HUMAN = 1;
    private final static int AI = 2;
    private final int[][] boardGrid;

    private int currentPlayer;


    public Board() {
        boardGrid = new int[SIZE][SIZE];
        initializeBoard();
        currentPlayer = AI;
    }

    private void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                boardGrid[i][j] = 0;
        }
        boardGrid[3][3] = 1;
        boardGrid[3][4] = 2;
        boardGrid[4][3] = 2;
        boardGrid[4][4] = 1;

    }

    public int getPoints(int player){
        if (player == HUMAN || player == AI)
            return countDiscs(player);
        throw new IllegalArgumentException();
    }

    public int getTotalDiscs(){
        return TOTAL_CELLS - countDiscs(0);
    }

    public List<Cell> validMoves(int player){
        ArrayList<Cell> cells = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                if (!wonDiscs(i, j).isEmpty())
                    cells.add(new Cell(i, j));
        }
        return cells;
    }

    public boolean hasMoves(int player){
        return !validMoves(player).isEmpty();
    }

    public void makeMove(int x, int y) {
        if (isValidMove(x, y)) {
            List<Disc> wonDiscs = wonDiscs(x, y);
            if (!wonDiscs.isEmpty()) {
                boardGrid[x][y] = currentPlayer;
                flipDiscs(wonDiscs);
                changeTurn();
            }
        }
    }

    private int countDiscs(int player){
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                if (boardGrid[i][j] == player)
                    count++;
        }
        return count;
    }

    private void flipDiscs(List<Disc> discs) {
        for (Disc d : discs) {
            d.flip();
            boardGrid[d.x()][d.y()] = d.color();
        }

    }

    private boolean isValidMove(int x, int y) {
        return cellIsEmpty(x, y) && cellExists(x, y);
    }


    // really ugly, find solution for duplicates if you can
    private List<Disc> wonDiscs(int x, int y) {
        List<Disc> discs = new ArrayList<>();
        List<Disc> temp = new ArrayList<>();
        // check left
        int i = y - 1;
        while (i >= 0) {
            if (boardGrid[x][i] != 0 && boardGrid[x][i] != currentPlayer)
                temp.add(new Disc(x, i, boardGrid[x][i]));
            else {
                if (boardGrid[x][i] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i--;
        }
        // check right
        i = y + 1;
        while (i < SIZE) {
            if (boardGrid[x][i] != 0 && boardGrid[x][i] != currentPlayer)
                temp.add(new Disc(x, i, boardGrid[x][i]));
            else {
                if (boardGrid[x][i] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;

            }
            i++;
        }
        // check up
        i = x - 1;
        while (i >= 0) {
            if (boardGrid[i][y] != 0 && boardGrid[i][y] != currentPlayer)
                temp.add(new Disc(i, y, boardGrid[i][y]));
            else {
                if (boardGrid[i][y] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i--;
        }
        // check down
        i = x + 1;
        while (i < SIZE) {
            if (boardGrid[i][y] != 0 && boardGrid[i][y] != currentPlayer)
                temp.add(new Disc(i, y, boardGrid[i][y]));
            else {
                if (boardGrid[i][y] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i++;
        }
        // check left-up
        i = y - 1;
        int k = x - 1;
        while (i >= 0 && k >= 0) {
            if (boardGrid[k][i] != 0 && boardGrid[k][i] != currentPlayer)
                temp.add(new Disc(k, i, boardGrid[k][i]));
            else {
                if (boardGrid[k][i] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i--;
            k--;
        }
        // check right-up
        i = y + 1;
        k = x - 1;
        while (i < SIZE && k >= 0) {
            if (boardGrid[k][i] != 0 && boardGrid[k][i] != currentPlayer)
                temp.add(new Disc(k, i, boardGrid[k][i]));
            else {
                if (boardGrid[k][i] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i++;
            k--;
        }
        // check left-down
        i = x + 1;
        k = y - 1;
        while (i < SIZE && k >= 0) {
            if (boardGrid[i][k] != 0 && boardGrid[i][k] != currentPlayer)
                temp.add(new Disc(i, k, boardGrid[i][k]));
            else {
                if (boardGrid[i][k] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i++;
            k--;
        }
        // check right-down
        i = x + 1;
        k = y + 1;
        while (i < SIZE && k < SIZE) {
            if (boardGrid[i][k] != 0 && boardGrid[i][k] != currentPlayer)
                temp.add(new Disc(i, k, boardGrid[i][k]));
            else {
                if (boardGrid[i][k] == currentPlayer)
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i++;
            k++;
        }

        return Collections.unmodifiableList(discs);
    }


    private void changeTurn() {
        currentPlayer = (currentPlayer == HUMAN) ? AI : HUMAN;
    }

    private boolean cellExists(int x, int y) {
        return !(x < 0 || x > SIZE - 1 || y < 0 || y > SIZE - 1);
    }

    private boolean cellIsEmpty(int x, int y){
        return boardGrid[x][y] == 0;
    }

    // gå igenom på papper hur den funkar, är typ som att den spelar mot sig själv. Videon hjälper!
    // pruning hjälper då en Nod redan vet att den har ett bättre alternativ och inte behöver
    // fortsätta söka genom sina barn
    // vad ska CPUn nästa drag vara? den kan "testa" sig fram med aloritmen och ta tillbaka
    // behöver nog inte ha noder som parameter, det ärför att förstå
    private int miniMax(/*Node node, */int depth, int alpha, int beta, boolean maxPlayer) {
        if (depth == 0) //or if game is over
            return 1; //evaluate result/score of this last node

        int evaluation;
        if (maxPlayer) {
            //find possible moves and do DFS
            int maxEval = Integer.MIN_VALUE; //start value, updates through for loop
            //for (Node child : possiblePositions){
            evaluation = miniMax(/*child, */depth - 1, alpha, beta, false);
            maxEval = Math.max(maxEval, evaluation);
            alpha = Math.max(alpha, evaluation);
            if (beta <= alpha) {
                //break;
            }
            //}
            return maxEval;
        } else {
            int minEval = Integer.MAX_VALUE; //start value, updates through for loop
            //for (Node n : possiblePositions){
            evaluation = miniMax(depth - 1, alpha, beta, true);
            minEval = Math.min(minEval, evaluation);
            beta = Math.min(beta, evaluation);
            if (beta <= alpha) {
                //break;
            }
            // }
            return minEval;
        }

    }

    public String toString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE - 1; j++) {
                sb.append(boardGrid[i][j]).append(" ");
            }
            sb.append(boardGrid[i][SIZE - 1]).append("\n");
        }
        return sb.toString();
    }

    public static void main(String[] args) {
        Board test = new Board();

        test.makeMove(3, 2);
        System.out.println(test);
        test.makeMove(2, 4);
        System.out.println(test);
        System.out.println(test.getPoints(AI));
        System.out.println(test.getPoints(HUMAN));
        System.out.println(test.getTotalDiscs());
    }
}
