import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * Class handeling board functionality
 *
 * @author August Johnson Palm
 */

//TODO: clean up, refactor, optimize, add comments

public class BoardHandler {

    private final static String TOP = "ABCDEFGH";
    private final static int SIZE = 8;

    private final int playerOne;
    private final int playerTwo;

    private int[][] boardGrid;

    private int currentPlayer;


    public BoardHandler(int playerOneID, int playerTwoID) {
        initializeBoard();
        playerOne = playerOneID;
        playerTwo = playerTwoID;
        currentPlayer = playerTwo;
    }

    private void initializeBoard() {
        boardGrid = new int[][]{
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 1, 2, 0, 0, 0},
                {0, 0, 0, 2, 1, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0},
                {0, 0, 0, 0, 0, 0, 0, 0}
        };
    }

    public boolean hasTurn(int player) throws PlayerException {
        if (isValidPlayerID(player))
            return currentPlayer == player;

        throw new PlayerException("Not a valid player ID");
    }

    //could optimize by just storing the score instead
    public int getPoints(int player) throws PlayerException, MoveException {
        if (isValidPlayerID(player))
            return countDiscs(player);

        throw new PlayerException("Not a valid player ID");
    }


    public List<Move> getValidMoves(int player) throws PlayerException, MoveException {
        if (!isValidPlayerID(player))
            throw new PlayerException("Not a valid player ID");

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
        return Collections.unmodifiableList(moves);
    }

    public int getMobility(int player) throws PlayerException, MoveException {
        return getValidMoves(player).size();
    }

    public boolean hasMoves(int player) throws PlayerException, MoveException {
        return getValidMoves(player).size() > 0;
    }


    public boolean makeMove(Move move, int player) throws MoveException, PlayerException {
        if (move == null)
            throw new MoveException("Move is null");
        if (!isValidPlayerID(player))
            throw new PlayerException("Not a valid player ID");

        if (isAvailableCell(move.row(), move.column())) {
            List<Disc> wonDiscs = wonDiscs(
                    move.row(),
                    move.column(),
                    player);

            if (!wonDiscs.isEmpty()) {
                move.setWonDiscs(wonDiscs);
                doMove(move, player);
                return true;
            }
        }
        return false;
    }

    public void doMove(Move move, int player) throws MoveException, PlayerException {
        if (move == null)
            throw new MoveException("Move is null");
        if (!isValidPlayerID(player))
            throw new PlayerException("Not a valid player ID");

        setDisc(move.row(), move.column(), player);
        flipDiscs(move.getWonDiscs());
    }

    public void changeTurn() {
        if (currentPlayer == playerOne)
            currentPlayer = playerTwo;
        else
            currentPlayer = playerOne;
    }

    public boolean hasCell(int i, int j, int player) throws PlayerException, MoveException {
        if (!isValidPlayerID(player))
            throw new PlayerException("Not a valid player ID");
        if (!isInsideBoard(i, j))
            throw new MoveException("Not a valid row/column");

        return boardGrid[i][j] == player;

    }


    // game/evaluator class
    public boolean gameOver() throws PlayerException, MoveException {
        return !hasMoves(playerOne) && !hasMoves(playerTwo);
    }

    private int countDiscs(int player) throws PlayerException, MoveException {
        int count = 0;

        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                if (hasCell(i, j, player))
                    count++;
        }
        return count;
    }

    //could optimize by just storing the discs instead
    public List<Disc> getAllDiscs(int player) throws PlayerException, MoveException {
        List<Disc> discs = new ArrayList<>();
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                if (hasCell(i, j, player))
                    discs.add(new Disc(i, j, player));
        }
        return Collections.unmodifiableList(discs);
    }

    private void flipDiscs(List<Disc> discs) {
        for (Disc d : discs) {
            d.flip();
            setDisc(d.row(), d.column(), d.color());
        }

    }

    private boolean isAvailableCell(int i, int j) {
        return isInsideBoard(i, j) && cellIsEmpty(i, j);
    }

    private List<Disc> searchForWonDiscs(Direction direction, int i, int j, int player) throws PlayerException, MoveException {
        int opponent = player == playerTwo ? playerOne : playerTwo;

        List<Disc> discs = new ArrayList<>();

        //skip the cell we already stand on
        i += direction.dx;
        j += direction.dy;

        while (isInsideBoard(i, j)) {
            if (hasCell(i, j, opponent))
                discs.add(new Disc(i, j, opponent));

            else { // player has cell or it is empty we do not have to search further

                if (cellIsEmpty(i, j))
                    discs.clear(); // if the cell was empty we clear the discs (no discs were won)
                break;
            }
            i += direction.dx;
            j += direction.dy;
        }

        // we searched all the way outside the board
        if (!isInsideBoard(i, j))
            discs.clear();

        return Collections.unmodifiableList(discs);
    }


    private List<Disc> wonDiscs(int i, int j, int player) throws PlayerException, MoveException {

        List<Disc> discs = new ArrayList<>();

        for (Direction direction : Direction.values())
            discs.addAll(searchForWonDiscs(direction, i, j, player));

        return Collections.unmodifiableList(discs);
    }


    private void setDisc(int i, int j, int player) {
        boardGrid[i][j] = player;
    }


    public boolean cellIsEmpty(int i, int j) {
        return boardGrid[i][j] == 0;
    }


    private boolean isValidPlayerID(int player) {
        return player == playerOne || player == playerTwo || player == 0;
    }

    private boolean isInsideBoard(int i, int j) {
        return i >= 0 && i < SIZE && j >= 0 && j < SIZE;
    }


    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("    ");
        for (int i = 0; i < TOP.length(); i++)
            sb.append(TOP.charAt(i)).append(" ");
        sb.append("\n");

        for (int i = 0; i < SIZE; i++) {
            sb.append(i + 1).append(" | ");
            for (int j = 0; j < SIZE - 1; j++) {
                sb.append(boardGrid[i][j]).append(" ");
            }
            sb.append(boardGrid[i][SIZE - 1]).append("\n");
        }
        return sb.toString();
    }


    void setBoard(int[][] boardGrid) {
        this.boardGrid = boardGrid;
    }
}