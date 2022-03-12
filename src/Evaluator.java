import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;

public class Evaluator {

    private static final int MIN_PLAYER = 1;
    private static final int MAX_PLAYER = 2;

    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    private final BoardHandler boardHandler;
    private HashMap<Plane, HashSet<Disc>> planeStableDiscsMap;

    public Evaluator(BoardHandler bh) {
        boardHandler = bh;
    }

    public int getEvaluation(int difficulty) {

        switch (difficulty) {
            case EASY:
                return evaluateStaticWeight();
            case MEDIUM:
                return evaluateParity();
            case HARD:
                return evaluateParity() + evaluateMobility() + evaluateCornerValue();
            default:
                break;
        }
        throw new IllegalStateException();
    }

    private int evaluateStability(int player) {
        if (allCornersCellsEmpty())
            return 0;

        return -1;
    }

    private int evaluateParity() {
        int maxPlayerCoins = boardHandler.getPoints(MAX_PLAYER);
        int minPlayerCoins = boardHandler.getPoints(MIN_PLAYER);

        return 100 * (maxPlayerCoins - minPlayerCoins) / (maxPlayerCoins + minPlayerCoins);
    }

    private int evaluateMobility() {
        int maxPlayerMobility = boardHandler.getMobility(MAX_PLAYER);
        int minPlayerMobility = boardHandler.getMobility(MIN_PLAYER);

        if (maxPlayerMobility + minPlayerMobility == 0)
            return 0;

        return 100 * (maxPlayerMobility - minPlayerMobility) / (maxPlayerMobility + minPlayerMobility);
    }

    private int evaluateCornerValue() {
        int maxPlayerCornerValue = evaluateCornerValue(MAX_PLAYER);
        int minPlayerCornerValue = evaluateCornerValue(MIN_PLAYER);

        if (maxPlayerCornerValue + minPlayerCornerValue == 0)
            return 0;

        return 100 * (maxPlayerCornerValue - minPlayerCornerValue) / (maxPlayerCornerValue + minPlayerCornerValue);
    }

    private int evaluateCornerValue(int player) {
        int capturedCorners = 0;

        for (int i = 0, j = 0; j < 8; j += 7) {
            if (boardHandler.hasCell(i, j, player))
                capturedCorners++;
        }

        for (int i = 7, j = 7; j >= 0; j -= 7) {
            if (boardHandler.hasCell(i, j, player))
                capturedCorners++;
        }

        return capturedCorners;
    }

    private int evaluateStaticWeight() {
        int maxStaticWeight = evaluateStaticWeight(MAX_PLAYER);
        int minStaticWeight = evaluateStaticWeight(MIN_PLAYER);

        if (maxStaticWeight + minStaticWeight == 0)
            return 0;

        return 100 * (maxStaticWeight - minStaticWeight) / (maxStaticWeight + minStaticWeight);
    }

    private int evaluateStaticWeight(int player) {
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

        List<Move> validMovesMaxPlayer = boardHandler.getValidMoves(player);
        int staticMaxValue = 0;


        for (Move move : validMovesMaxPlayer)
            staticMaxValue += staticWeights[move.x()][move.y()];

        return staticMaxValue;
    }

    // messy, can be made prettier
    private enum Plane {
        HORIZONTAL, VERTICAL, DIAGONAL_LEFT_DOWN_RIGHT_UP, DIAGONAL_LEFT_UP_RIGHT_DOWN
    }

    private int evaluateDiscStability(int player) {

        planeStableDiscsMap = new HashMap<>();

        for (Plane plane : Plane.values())
            planeStableDiscsMap.put(plane, new HashSet<>());

        List<Disc> allDiscs = boardHandler.getAllDiscs(player);

        int stableDiscs = 0;
        for (Disc disc : allDiscs) {

            if (cellIsCorner(disc.i(), disc.j())) {
                stableDiscs++;
                continue;
            }

            boolean horizontal = planeStableDiscsMap.get(Plane.HORIZONTAL).contains(disc);
            boolean vertical = planeStableDiscsMap.get(Plane.VERTICAL).contains(disc);
            boolean diagonalLeftDownRightUp = planeStableDiscsMap.get(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP).contains(disc);
            boolean diagonalLeftUpRightDown = planeStableDiscsMap.get(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN).contains(disc);


            if (!horizontal)
                horizontal = stableInPlane(Plane.HORIZONTAL, disc, 0, -1, player); // check left
            if (!horizontal)
                horizontal = stableInPlane(Plane.HORIZONTAL, disc, 0, 1, player); // check right

            if (!vertical)
                vertical = stableInPlane(Plane.VERTICAL, disc, -1, 0, player); // check up
            if (!vertical)
                vertical = stableInPlane(Plane.VERTICAL, disc, 1, 0, player); // check down

            if (!diagonalLeftDownRightUp)
                diagonalLeftDownRightUp = stableInPlane(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP, disc, 1, -1, player);
            if (!diagonalLeftDownRightUp)
                diagonalLeftDownRightUp = stableInPlane(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP, disc, -1, 1, player);

            if (!diagonalLeftUpRightDown)
                diagonalLeftUpRightDown = stableInPlane(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN, disc, -1, -1, player);
            if (!diagonalLeftUpRightDown)
                diagonalLeftUpRightDown = stableInPlane(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN, disc, 1, 1, player);

            if (horizontal && vertical && diagonalLeftDownRightUp && diagonalLeftUpRightDown)
                stableDiscs++;
        }

        planeStableDiscsMap.clear();
        return stableDiscs;

    }

    private boolean stableInPlane(Plane plane, Disc disc, int dx, int dy, int player) {
        boolean stable = true;

        int i = disc.i();
        int j = disc.j();

        ArrayList<Disc> discs = new ArrayList<>();

        while (i >= 0 && i < 8 && j >= 0 && j < 8) {
            if (!boardHandler.hasCell(i, j, player)) {
                stable = false;
                break;
            }
            discs.add(disc);
            i += dx;
            j += dy;
        }
        if (stable)
            planeStableDiscsMap.get(plane).addAll(discs);

        return stable;
    }


    private boolean cellIsCorner(int i, int j) {
        return (i == 0 && j == 0) || (i == 0 && j == 7)
                || (i == 7 && j == 0) || (i == 7 && j == 7);
    }

    private boolean allCornersCellsEmpty() {
        return
                boardHandler.cellIsEmpty(0, 0) && boardHandler.cellIsEmpty(0, 1)
                        && boardHandler.cellIsEmpty(1, 0) && boardHandler.cellIsEmpty(1, 1)
                        && boardHandler.cellIsEmpty(0, 7) && boardHandler.cellIsEmpty(0, 6)
                        && boardHandler.cellIsEmpty(1, 7) && boardHandler.cellIsEmpty(1, 6)
                        && boardHandler.cellIsEmpty(7, 0) && boardHandler.cellIsEmpty(6, 0)
                        && boardHandler.cellIsEmpty(7, 1) && boardHandler.cellIsEmpty(6, 1)
                        && boardHandler.cellIsEmpty(7, 7) && boardHandler.cellIsEmpty(7, 6)
                        && boardHandler.cellIsEmpty(6, 7) && boardHandler.cellIsEmpty(6, 6);
    }


}
