public class Evaluator {

    private static final int MIN_PLAYER = 1;
    private static final int MAX_PLAYER = 2;
    // should be able to take in board and evaluate scores

    private int evaluateStability(int player){


        return -1;
    }

    private int evaluatePointsDifference(int player){
        return -1;
    }

    private int evaluateMobility(int player){
        return -1;
    }

    private int evaluateCornerValue(int player, BoardHandler bh){
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
    }

    private int evaluateAdjacentCornerValue(int player, BoardHandler bh){


        return 25;
    }


}
