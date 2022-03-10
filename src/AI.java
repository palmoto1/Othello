import java.util.List;

public class AI {

    private static final int MIN_PLAYER = 1;
    private static final int MAX_PLAYER = 2;

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
        searchNextOptimalMove(5, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_PLAYER);
    }

    private int searchNextOptimalMove(int depth, int alpha, int beta, int player) {
        if (depth > 0 && !bh.gameOver()) {

            List<Move> validMoves;
            int evaluation;
            int bestEvaluation;
            int nextPlayer;

            if (player == MAX_PLAYER) {
                validMoves = bh.getValidMoves(MAX_PLAYER);
                bestEvaluation = Integer.MIN_VALUE;
                nextPlayer = MIN_PLAYER;
            } else {
                validMoves = bh.getValidMoves(MIN_PLAYER);
                bestEvaluation = Integer.MAX_VALUE;
                nextPlayer = MAX_PLAYER;
            }
            Move optimal = validMoves.get(0); // choosing the first move as default optimal value

            // loop through all valid moves
            for (Move move : validMoves) {
                bh.doMove(move, player); // test the move

                //recursive call to next level
                evaluation = searchNextOptimalMove(depth - 1, alpha, beta, nextPlayer);

                bh.doMove(move, 0); // undo the move so it dont affect the actual game

                if (player == MAX_PLAYER) {
                    alpha = Math.max(alpha, evaluation); // update alpha
                    if (evaluation > bestEvaluation) {
                        bestEvaluation = evaluation;
                        optimal = move; // the current move has the current best evaluation for max player
                    }

                } else { // min player
                    beta = Math.min(beta, evaluation); // update beta
                    if (evaluation < bestEvaluation) {
                        bestEvaluation = evaluation;
                        optimal = move; // the current move has the current best evaluation for min player
                    }
                }

                if (beta <= alpha)
                    break;
            }

            nextMove = optimal;
            return bestEvaluation;
        }

        return bh.getPointsDifference(MAX_PLAYER); // need better heurestic
    }

}
