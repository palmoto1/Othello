import java.util.*;

public class BoardHandler {

    private enum Direction {
        LEFT, RIGHT, UP, DOWN, LEFT_UP, RIGHT_UP, LEFT_DOWN, RIGHT_DOWN
    }

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

    // game class
    public boolean hasTurn(int player) {
        return currentPlayer == player;
    }

    //
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

    public boolean makeMove(int x, int y, int player) {
        if (isAvailableCell(x, y)) {
            List<Disc> wonDiscs = wonDiscs(x, y, player);
            if (!wonDiscs.isEmpty()) {
                doMove(new Move(x, y, wonDiscs), player);
                return true;
                //changeTurn();
            }
        }
        return false;
    }


    // evaluator class
    public int getPointsDifference(int player) {
        int other = (player == AI) ? HUMAN : AI;
        return getPoints(player) - getPoints(other);
    }

    public void doMove(Move move, int player) {
        setDisc(move.x(), move.y(), player);
        flipDiscs(move.getWonDiscs());
    }

    //game class
    public void changeTurn() {
        if (currentPlayer == HUMAN) {
            currentPlayer = AI;
            currentOpponent = HUMAN;
        } else {
            currentPlayer = HUMAN;
            currentOpponent = AI;
        }
    }


    // game/evaluator class
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
        return cellExists(x, y) && cellIsEmpty(x, y);
    }

    private List<Disc> searchForWonDiscs(Direction direction, int i, int j, int player) {

        int dx;
        int dy;

        switch (direction) {
            case LEFT:
                dx = 0;
                dy = -1;
                break;
            case RIGHT:
                dx = 0;
                dy = 1;
                break;
            case UP:
                dx = -1;
                dy = 0;
                break;
            case DOWN:
                dx = 1;
                dy = 0;
                break;
            case LEFT_DOWN:
                dx = 1;
                dy = -1;
                break;
            case LEFT_UP:
                dx = -1;
                dy = -1;
                break;
            case RIGHT_DOWN:
                dx = 1;
                dy = 1;
                break;
            case RIGHT_UP:
                dx = -1;
                dy = 1;
                break;
            default:
                dx = dy = 0;
        }


        int opponent = player == AI ? HUMAN : AI;

        List<Disc> discs = new ArrayList<>();

        while (i >= 0 && i < SIZE && j >= 0 && j < SIZE) {
            if (hasCell(i, j, opponent))
                discs.add(new Disc(i, j, opponent));
            else {
                if (hasCell(i, j, player))
                    return Collections.unmodifiableList(discs);
                discs.clear();
                break;
            }
            i += dx;
            j += dy;
        }
        return Collections.unmodifiableList(discs);
    }


    private List<Disc> wonDiscs(int i, int j, int player) {

        List<Disc> discs = new ArrayList<>();

        discs.addAll(searchForWonDiscs(Direction.LEFT, i, j - 1, player));
        discs.addAll(searchForWonDiscs(Direction.RIGHT, i, j + 1, player));
        discs.addAll(searchForWonDiscs(Direction.UP, i - 1, j, player));
        discs.addAll(searchForWonDiscs(Direction.DOWN, i + 1, j, player));
        discs.addAll(searchForWonDiscs(Direction.LEFT_UP, i - 1, j - 1, player));
        discs.addAll(searchForWonDiscs(Direction.LEFT_DOWN, i + 1, j - 1, player));
        discs.addAll(searchForWonDiscs(Direction.RIGHT_UP, i - 1, j + 1, player));
        discs.addAll(searchForWonDiscs(Direction.RIGHT_DOWN, i + 1, j + 1, player));


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