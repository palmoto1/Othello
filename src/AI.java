import java.util.List;

public class AI {

    private static final int MIN_PLAYER = 1;
    private static final int MAX_PLAYER = 2;
    private static final int SEARCH_DEPTH = 5;

    private final BoardHandler boardHandler;
    private final Evaluator evaluator;
    private int difficulty;
    private Move nextMove;


    public AI(BoardHandler boardHandler, int difficulty){
        this.boardHandler = boardHandler;
        this.difficulty = difficulty;
        evaluator = new Evaluator(boardHandler);
    }

    public Move getNextMove() {
        return nextMove;
    }

    public void setNextMove(){
        searchNextOptimalMove(SEARCH_DEPTH, Integer.MIN_VALUE, Integer.MAX_VALUE, MAX_PLAYER);
    }

    private int searchNextOptimalMove(int depth, int alpha, int beta, int player) {
        if (depth > 0 && !boardHandler.gameOver()) {

            List<Move> validMoves;
            int evaluation;
            int bestEvaluation;
            int nextPlayer;

            if (player == MAX_PLAYER) {
                validMoves = boardHandler.getValidMoves(MAX_PLAYER);
                bestEvaluation = Integer.MIN_VALUE;
                nextPlayer = MIN_PLAYER;
            } else {
                validMoves = boardHandler.getValidMoves(MIN_PLAYER);
                bestEvaluation = Integer.MAX_VALUE;
                nextPlayer = MAX_PLAYER;
            }

            if (validMoves.isEmpty())
                System.out.println("Its empty");
            Move optimal = null; // choosing the first move as default optimal value

            // loop through all valid moves
            for (Move move : validMoves) {
                boardHandler.doMove(move, player); // test the move

                //recursive call to next level
                evaluation = searchNextOptimalMove(depth - 1, alpha, beta, nextPlayer);

                boardHandler.doMove(move, 0); // undo the move so it dont affect the actual game

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

        return evaluator.getEvaluation(difficulty);
    }

}
