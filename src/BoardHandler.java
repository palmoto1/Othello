import java.util.*;

public class BoardHandler {

    //TODO: had a weird game, maybe some bug somwhere? split up into classes and make GUI

    private final static int SIZE = 8;
    private final static int TOTAL_CELLS = 64;
    private final static int HUMAN = 1;
    private final static int AI = 2;
    private final int[][] boardGrid;

    private int currentPlayer;
    private int currentOpponent;


    public BoardHandler() {
        boardGrid = new int[SIZE][SIZE];
        initializeBoard();
        currentPlayer = AI;
        currentOpponent = HUMAN;
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

    public int getCurrentPlayer() {
        return currentPlayer;
    }

    public int getCurrentOpponent() {
        return currentOpponent;
    }

    public int getPoints(int player) {
        if (player == HUMAN || player == AI)
            return countDiscs(player);
        throw new IllegalArgumentException();
    }

    public int getTotalDiscs() {
        return TOTAL_CELLS - countDiscs(0);
    }

    public List<Move> getValidMoves(int player) {
        List<Move> moves = new ArrayList<>();
        List<Disc> wonDiscs;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++) {
                if (isAvailableCell(i, j)) {
                    wonDiscs = wonDiscs(i, j, player);
                    if (!wonDiscs.isEmpty())
                        moves.add(new Move(i, j, wonDiscs));
                }
            }
        }
        return moves;
    }

    public boolean hasMoves(int player) {
        return !getValidMoves(player).isEmpty();
    }

    public void makeMove(int x, int y, int player) {
        if (isAvailableCell(x, y)) {
            List<Disc> wonDiscs = wonDiscs(x, y, player);
            if (!wonDiscs.isEmpty()) {
                doMove(new Move(x, y, wonDiscs), player);
                //changeTurn();
            }
        }
    }

    public int getPointsDifference(int player) {
        int other = (player == AI) ? HUMAN : AI;
        return getPoints(player) - getPoints(other);
    }

    public void doMove(Move move, int player) {
        setDisc(move.x(), move.y(), player);
        flipDiscs(move.getWonDiscs());
    }

    public void changeTurn() {
        if (currentPlayer == HUMAN) {
            currentPlayer = AI;
            currentOpponent = HUMAN;
        } else {
            currentPlayer = HUMAN;
            currentOpponent = AI;
        }
    }

    public boolean gameOver() {
        return !hasMoves(HUMAN) && !hasMoves(AI);
    }

    private int countDiscs(int player) {
        int count = 0;
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                if (hasCell(i, j, player))
                    count++;
        }
        return count;
    }

    private void flipDiscs(List<Disc> discs) {
        for (Disc d : discs) {
            d.flip();
            setDisc(d.x(), d.y(), d.color());
        }

    }

    private boolean isAvailableCell(int x, int y) {
        return cellIsEmpty(x, y) && cellExists(x, y);
    }


    // really ugly, find solution for duplicates if you can
    // had a weird game, maybe some bug somwhere?
    private List<Disc> wonDiscs(int x, int y, int player) {
        int opponent = player == AI ? HUMAN : AI;

        List<Disc> discs = new ArrayList<>();
        List<Disc> temp = new ArrayList<>();

        // check left
        int i = y - 1;
        while (i >= 0) {
            if (hasCell(x, i, opponent))
                temp.add(new Disc(x, i, opponent));
            else {
                if (hasCell(x, i, player))
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i--;
        }
        // check right
        i = y + 1;
        while (i < SIZE) {
            if (hasCell(x, i, opponent))
                temp.add(new Disc(x, i, opponent));
            else {
                if (hasCell(x, i, player))
                    discs.addAll(temp);
                temp.clear();
                break;

            }
            i++;
        }
        // check up
        i = x - 1;
        while (i >= 0) {
            if (hasCell(i, y, opponent))
                temp.add(new Disc(i, y, opponent));
            else {
                if (hasCell(i, y, player))
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i--;
        }
        // check down
        i = x + 1;
        while (i < SIZE) {
            if (hasCell(i, y, opponent))
                temp.add(new Disc(i, y, opponent));
            else {
                if (hasCell(i, y, player))
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
            if (hasCell(k, i, opponent))
                temp.add(new Disc(k, i, opponent));
            else {
                if (hasCell(k, i, player))
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
            if (hasCell(k, i, opponent))
                temp.add(new Disc(k, i, opponent));
            else {
                if (hasCell(k, i, player))
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
            if (hasCell(i, k, opponent))
                temp.add(new Disc(i, k, opponent));
            else {
                if (hasCell(i, k, player))
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
            if (hasCell(i, k, opponent))
                temp.add(new Disc(i, k, opponent));
            else {
                if (hasCell(i, k, player))
                    discs.addAll(temp);
                temp.clear();
                break;
            }
            i++;
            k++;
        }

        return Collections.unmodifiableList(discs);
    }


    private void setDisc(int x, int y, int player) {
        boardGrid[x][y] = player;
    }

    private boolean hasCell(int x, int y, int player) {
        return boardGrid[x][y] == player;
    }

    private boolean cellExists(int x, int y) {
        return !(x < 0 || x > SIZE - 1 || y < 0 || y > SIZE - 1);
    }

    private boolean cellIsEmpty(int x, int y) {
        return boardGrid[x][y] == 0;
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
}

            /*if (player == AI) {
                validMoves = getValidMoves(AI);
                //find possible moves and do DFS
                int maxEvaluation = Integer.MIN_VALUE; //start value, updates through for loop
                for (Move move : validMoves) {
                    doMove(move, AI);

                    evaluation = miniMax(depth - 1, alpha, beta, HUMAN);

                    doMove(move, 0);

                    if (evaluation > maxEvaluation) {
                        maxEvaluation = evaluation;
                        optimal = move;
                    }

                    alpha = Math.max(alpha, evaluation);
                    if (beta <= alpha)
                        break;
                }
                nextAIMove = optimal;
                return maxEvaluation;

            } else {
                validMoves = getValidMoves(HUMAN);
                int minEvaluation = Integer.MAX_VALUE; //start value, updates through for loop
                for (Move move : validMoves) {
                    doMove(move, HUMAN);

                    evaluation = miniMax(depth - 1, alpha, beta, AI);

                    doMove(move, 0); //redoing the previous move

                    //minEvaluation = Math.min(minEvaluation, evaluation);
                    if (evaluation < minEvaluation) {
                        minEvaluation = evaluation;
                        optimal = move;
                    }

                    beta = Math.min(beta, evaluation);
                    if (beta <= alpha)
                        break;
                }
                nextAIMove = optimal;
                return minEvaluation;
            }
        }*/
