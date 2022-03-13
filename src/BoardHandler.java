import java.util.*;

public class BoardHandler {

    private final static int SIZE = 8;
    private final static int TOTAL_CELLS = 64;
    private final static int HUMAN = 1;
    private final static int AI = 2;
    private final int[][] boardGrid;
    private int noOfDiscs = 0;

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
        return TOTAL_CELLS - noOfDiscs;
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

    public int getMobility(int player){
        return getValidMoves(player).size();
    }

    public boolean hasMoves(int player) {
        return getValidMoves(player).size() > 0;
    }

    //Should take a Move as param instead
    public boolean makeMove(int i, int j, int player) {
        if (isAvailableCell(i, j)) {
            List<Disc> wonDiscs = wonDiscs(i, j, player);
            if (!wonDiscs.isEmpty()) {
                doMove(new Move(i, j, wonDiscs), player);
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
        noOfDiscs++;
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

    public boolean hasCell(int i, int j, int player) {
        return boardGrid[i][j] == player;
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

    public List<Disc> getAllDiscs(int player) {
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
            setDisc(d.i(), d.j(), d.color());
        }

    }

    private boolean isAvailableCell(int i, int j) {
        return cellExists(i, j) && cellIsEmpty(i, j);
    }

    public List<Disc> searchForWonDiscs(Direction direction, int i, int j, int player) {

        int opponent = player == AI ? HUMAN : AI;

        List<Disc> discs = new ArrayList<>();

        //skip the cell we already stand on
        i+=direction.dx;
        j+=direction.dy;

        while (i >= 0 && i < SIZE && j >= 0 && j < SIZE) {
            if (hasCell(i, j, opponent))
                discs.add(new Disc(i, j, opponent));
            else { // player has cell or it is empty we do not have to search further
                if (cellIsEmpty(i,j))
                    discs.clear(); // if the cell was empty we clear the discs (no discs were won)
                break;
            }
            i += direction.dx;
            j += direction.dy;
        }

        if (!(i >= 0 && i < SIZE && j >= 0 && j < SIZE))
            discs.clear();
        
        return Collections.unmodifiableList(discs);
    }


    private List<Disc> wonDiscs(int i, int j, int player) {

        List<Disc> discs = new ArrayList<>();

        for (Direction direction : Direction.values())
            discs.addAll(searchForWonDiscs(direction, i, j, player));


        return Collections.unmodifiableList(discs);
    }


    private void setDisc(int i, int j, int player) {
        boardGrid[i][j] = player;
    }

    private boolean cellExists(int i, int j) {
        return !(i < 0 || i > SIZE - 1 || j < 0 || j > SIZE - 1);
    }

    public boolean cellIsEmpty(int i, int j) {
        return boardGrid[i][j] == 0;
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