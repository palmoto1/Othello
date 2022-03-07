import java.util.*;

public class Board {

    private final static int SIZE = 8;
    private final static int PLAYER = 1;
    private final static int AI = 2;
    private final int[][] boardGrid;
    private int turn;

    public Board() {
        boardGrid = new int[SIZE][SIZE];
        initializeBoard();
        turn = AI;

    }

    public void initializeBoard() {
        for (int i = 0; i < SIZE; i++) {
            for (int j = 0; j < SIZE; j++)
                boardGrid[i][j] = 0;
        }
        boardGrid[3][3] = 1;
        boardGrid[3][4] = 2;
        boardGrid[4][3] = 2;
        boardGrid[4][4] = 1 ;

    }

    public void makeMove(int x, int y) {
        if (!validCell(x, y))
            return;

        boardGrid[x][y] = turn;
        flipDiscs(getAffectedDiscs(x, y));
        changeTurn();


    }


    public void flipDiscs(List<Disc> affectedDiscs) {
        for (Disc d : affectedDiscs) {
            d.flip();
            boardGrid[d.x()][d.y()] = d.color();
        }


    }

    public boolean validCell(int x, int y) {
        return boardGrid[x][y] == 0 && cellInsideBoard(x, y) &&
                !getAffectedDiscs(x, y).isEmpty();
    }


    // really ugly, find solution for duplicates if you can
    public List<Disc> getAffectedDiscs(int x, int y) {
        List<Disc> discs = new ArrayList<>();
        List<Disc> temp = new ArrayList<>();
        // check left
        int i = y - 1;
        while (i >= 0) {
            if (boardGrid[x][i] != 0 && boardGrid[x][i] != turn)
                temp.add(new Disc(x, i, boardGrid[x][i]));
            else {
                if (boardGrid[x][i] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                }
                break;
            }
            i--;
        }
        // check right
        i = y + 1;
        while (i < SIZE) {
            if (boardGrid[x][i] != 0 && boardGrid[x][i] != turn)
                temp.add(new Disc(x, i, boardGrid[x][i]));
            else {
                if (boardGrid[x][i] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                }
                break;

            }
            i++;
        }
        // check up
        i = x - 1;
        while (i >= 0) {
            if (boardGrid[i][y] != 0 && boardGrid[i][y] != turn)
                temp.add(new Disc(i, y, boardGrid[i][y]));
            else {
                if (boardGrid[i][y] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                }
                break;
            }
            i--;
        }
        // check down
        i = x + 1;
        while (i < SIZE) {
            if (boardGrid[i][y] != 0 && boardGrid[i][y] != turn)
                temp.add(new Disc(i, y, boardGrid[i][y]));
            else {
                if (boardGrid[i][y] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                    break;
                }
            }
            i++;
        }
        // check left-up
        i = y - 1;
        int k = x - 1;
        while (i >= 0 && k >= 0) {
            if (boardGrid[k][i] != 0 && boardGrid[k][i] != turn)
                temp.add(new Disc(k, i, boardGrid[k][i]));
            else {
                if (boardGrid[k][i] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                    break;
                }
            }
            i--;
            k--;
        }
        // check right-up
        i = y + 1;
        k = x - 1;
        while (i < SIZE && k >= 0) {
            if (boardGrid[k][i] != 0 && boardGrid[k][i] != turn)
                temp.add(new Disc(k, i, boardGrid[k][i]));
            else {
                if (boardGrid[k][i] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                    break;
                }
            }
            i++;
            k--;
        }
        // check left-down
        i = x + 1;
        k = y - 1;
        while (i < SIZE && k >= 0) {
            if (boardGrid[i][k] != 0 && boardGrid[i][k] != turn)
                temp.add(new Disc(i, k, boardGrid[i][k]));
            else {
                if (boardGrid[i][k] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                    break;
                }
            }
            i++;
            k--;
        }
        // check right-down
        i = x + 1;
        k = y + 1;
        while (i < SIZE && k < SIZE) {
            if (boardGrid[i][k] != 0 && boardGrid[i][k] != turn)
                temp.add(new Disc(i, k, boardGrid[i][k]));
            else {
                if (boardGrid[i][k] == turn) {
                    discs.addAll(temp);
                    temp.clear();
                    break;
                }
            }
            i++;
            k++;
        }

        return discs;
    }


    public void changeTurn() {
        turn = (turn == PLAYER) ? AI : PLAYER;
    }

    private boolean cellInsideBoard(int x, int y) {
        return !(x < 0 || x > SIZE - 1 || y < 0 || y > SIZE - 1);
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

        test.makeMove(2, 5);
        System.out.println(test);
    }
}
