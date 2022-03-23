import java.util.List;

/**
 * Class representing the AI
 *
 * @author August Johnson Palm
 */


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

    /**
     * Using the MiniMax algorithm.
     * It uses the current state of the game to decide the AIs next move by looking ahead on future possible outcomes of
     * the moves possible for the current state of the game.
     * The method will traverse recursively until depth zero is reached and return an evaluation based on a
     * heuristic which is chosen by the difficulty of the game. The result will be passed up in the recursion and
     * compared to other evaluations of other possible moves on each level. The one to get chosen on a specific
     * depth depends on if it is the optimal move for the AI or its opponent. The AI has a role as the maximizing
     * player and always choose the highest evaluation while its opponent is the minimizing player, always choosing the
     * lowest evaluation. These two alternate as the player on every other level of depth.
     * The algorithm uses alpha beta pruning to minimize the moves needed to be searched. By pruning we can eliminate
     * to test the outcome of moves that is certain to be worse than a previously evaluated move.
     *
     * @param depth the search depth it will traverse to.
     * @param alpha the current best evaluation for the AI
     * @param beta the current best evaluation for the opponent
     * @param player the player ID
     *
     * @return An evaluation depending on the difficulty of the game. This is handled in the Evaluator class
     *
     * @throws PlayerException if the player ID is invalid
     * @throws MoveException if null is passed as an argument into the boardHandler.doMove()
     */

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

            Move optimal = null;

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
