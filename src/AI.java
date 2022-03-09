import java.util.List;

public class AI {

    private static final int ID = 2;
    private final BoardHandler bh;
    private int difficulty;
    private Move nextMove;


    public AI(BoardHandler bh, int difficulty){
        this.bh = bh;
        this.difficulty = difficulty;
    }

    public Move getNextMove() {
        return nextMove;
    }

    public void setNextMove(){
        searchNextOptimalMove(5, Integer.MIN_VALUE, Integer.MAX_VALUE, ID);
    }

    private int searchNextOptimalMove(int depth, int alpha, int beta, int player) {
        if (depth > 0 && !bh.gameOver()) {

            List<Move> validMoves;
            int evaluation;
            int bestEvaluation;
            int nextPlayer;

            if (player == ID) {
                validMoves = bh.getValidMoves(ID);
                bestEvaluation = Integer.MIN_VALUE;
                nextPlayer = 1;
            } else {
                validMoves = bh.getValidMoves(1);
                bestEvaluation = Integer.MAX_VALUE;
                nextPlayer = ID;
            }

            Move optimal = validMoves.get(0);

            for (Move move : validMoves) {
                bh.doMove(move, player);

                evaluation = searchNextOptimalMove(depth - 1, alpha, beta, nextPlayer);

                bh.doMove(move, 0);

                if (player == ID) {
                    alpha = Math.max(alpha, evaluation);
                    if (evaluation > bestEvaluation) {
                        bestEvaluation = evaluation;
                        optimal = move;
                    }

                } else {
                    beta = Math.min(beta, evaluation);
                    if (evaluation < bestEvaluation) {
                        bestEvaluation = evaluation;
                        optimal = move;
                    }
                }

                if (beta <= alpha)
                    break;
            }

            nextMove = optimal;
            return bestEvaluation;
        }
        return bh.getPointsDifference(2);
    }

}
