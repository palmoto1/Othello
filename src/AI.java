import java.util.*;

public class AI {


    private static final int SEARCH_DEPTH = 5;

    private final BoardHandler boardHandler;
    private final Evaluator evaluator;

    private final int minPlayerID;
    private final int maxPlayerID;
    private final int difficulty;

    private Move nextMove;


    public AI(BoardHandler boardHandler, int difficulty, int minPlayerID, int maxPlayerID){
        this.boardHandler = boardHandler;
        this.difficulty = difficulty;
        this.minPlayerID = minPlayerID;
        this.maxPlayerID = maxPlayerID;
        evaluator = new Evaluator(boardHandler, minPlayerID, maxPlayerID);
    }

    public Move getNextMove() {
        return nextMove;
    }

    public void setNextMove() throws MoveException, PlayerException {
        searchNextOptimalMove(SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, maxPlayerID);
    }

    private int searchNextOptimalMove(int depth, int alpha, int beta, int player) throws PlayerException, MoveException {
        if (depth > 0 && !boardHandler.gameOver()) {

            List<Move> validMoves;
            int evaluation;
            int bestEvaluation;
            int nextPlayer;

            if (player == maxPlayerID) {
                validMoves = boardHandler.getValidMoves(maxPlayerID);
                bestEvaluation = Integer.MIN_VALUE;
                nextPlayer = minPlayerID;
            } else {
                validMoves = boardHandler.getValidMoves(minPlayerID);
                bestEvaluation = Integer.MAX_VALUE;
                nextPlayer = maxPlayerID;
            }

            Move optimal = null; // choosing the first move as default optimal value

            // loop through all valid moves
            for (Move move : validMoves) {
                boardHandler.doMove(move, player); // test the move

                //recursive call to next level
                evaluation = searchNextOptimalMove(depth - 1, alpha, beta, nextPlayer);

                boardHandler.doMove(move, 0); // undo the move so it dont affect the actual game

                if (player == maxPlayerID) {
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

        return evaluator.getEvaluation(difficulty);
    }

}
