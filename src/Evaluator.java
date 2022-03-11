import java.util.List;

public class Evaluator {

    private static final int MIN_PLAYER = 1;
    private static final int MAX_PLAYER = 2;
    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    private final BoardHandler boardHandler;

    public Evaluator(BoardHandler bh){
        boardHandler = bh;
    }

    public int getEvaluation(int difficulty){

        switch (difficulty) {
            case EASY:
                return evaluateStaticWeight();
            case MEDIUM:
                return evaluateParity();
            case HARD:
                return evaluateParity() + evaluateMobility();
            default:
                break;
        }
        throw new IllegalStateException();
    }

    private int evaluateStability(){
        return -1;
    }

    private int evaluateParity(){
        int maxPlayerCoins = boardHandler.getPoints(MAX_PLAYER);
        int minPlayerCoins = boardHandler.getPoints(MIN_PLAYER);

        return 100 * (maxPlayerCoins-minPlayerCoins)/(maxPlayerCoins+minPlayerCoins);
    }

    private int evaluateMobility(){
        int maxPlayerMobility = boardHandler.getPoints(MAX_PLAYER);
        int minPlayerMobility = boardHandler.getPoints(MIN_PLAYER);

        if (maxPlayerMobility+minPlayerMobility == 0)
            return 0;
        return 100 * (maxPlayerMobility-minPlayerMobility)/(maxPlayerMobility+minPlayerMobility);
    }

   /* private int evaluateCornerValue(BoardHandler bh){
        int capturedCorners = 0;

        for (int i = 0, j = 0; j < 8; j+=7){
            if (bh.hasCell(i, j, MAX_PLAYER))
                capturedCorners++;
            else if (bh.hasCell(i, j, MIN_PLAYER))
                capturedCorners--;
        }

        for (int i = 7, j = 7; j >= 0; j-=7){
            if (bh.hasCell(i, j, MAX_PLAYER))
                capturedCorners++;
            else if (bh.hasCell(i, j, MIN_PLAYER))
                capturedCorners--;
        }

        return 25 * capturedCorners;
    }*/

    private int evaluateStaticWeight(){
        int maxStaticWeight = evaluateStaticWeight(MAX_PLAYER);
        int minStaticWeight = evaluateStaticWeight(MIN_PLAYER);

        if (maxStaticWeight + minStaticWeight == 0)
            return 0;

        return 100 * (maxStaticWeight - minStaticWeight)/(maxStaticWeight + minStaticWeight);
    }

   private int evaluateStaticWeight(int player){
       int[][] staticWeights = {
               {20, -15, 10, 10, 10, 10, -15, 20},
               {-15, -20, -5, -5, -5, -5, -20, -15},
               {10, -5, 5, 0, 0, 5, -5, 10},
               {10, -5, 0, 5, 5, 0, -5, 10},
               {10, -5, 0, 5, 5, 0, -5, 10},
               {10, -5, 5, 0, 0, 5, -5, 10},
               {-15, -20, -5, -5, -5, -5, -20, -15},
               {20, -15, 10, 10, 10, 10, -15, 20}
       };

       List<Move> validMovesMaxPlayer = boardHandler.getValidMoves(MAX_PLAYER);
       int staticMaxValue = 0;


       for (Move move : validMovesMaxPlayer)
           staticMaxValue += staticWeights[move.x()][move.y()];

       return staticMaxValue;
   }


}
