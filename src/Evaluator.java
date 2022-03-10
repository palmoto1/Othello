public class Evaluator {


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
            if (bh.hasCell(i, j, 2))
                capturedCorners++;
            else if (bh.hasCell(i, j, 1))
                capturedCorners--;
        }

        for (int i = 7, j = 7; j >= 0; j-=7){
            if (bh.hasCell(i, j, 2))
                capturedCorners++;
            else if (bh.hasCell(i, j, 1))
                capturedCorners--;
        }

        return 25 * capturedCorners;
    }

    private int evaluateAdjacentCornerValue(int player, BoardHandler bh){


        return 25;
    }


}
