
import java.util.*;

public class Evaluator {

    private enum Plane {
        HORIZONTAL, VERTICAL, DIAGONAL_LEFT_DOWN_RIGHT_UP, DIAGONAL_LEFT_UP_RIGHT_DOWN
    }

    private static final int MIN_PLAYER = 1;
    private static final int MAX_PLAYER = 2;

    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    private final BoardHandler boardHandler;
    private HashMap<Plane, HashSet<Disc>> discsStableInPlanes;

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
                return evaluateStability();
               // return evaluateParity() + evaluateMobility() + evaluateCornerValue();
            default:
                break;
        }
        throw new IllegalStateException();
    }

    private int evaluateStability() {
        if (allCornersCellsEmpty())
            return 0;

        int maxPlayerStability = evaluateStability(MAX_PLAYER);
        int minPlayerStability = evaluateStability(MIN_PLAYER);

        if (maxPlayerStability + minPlayerStability == 0)
            return 0;

        return 100 * (maxPlayerStability - minPlayerStability) / (maxPlayerStability + minPlayerStability);
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

    private int evaluateStability (int player) {

        discsStableInPlanes = new HashMap<>();

        for (Plane plane : Plane.values())
            discsStableInPlanes.put(plane, new HashSet<>());

        List<Disc> allDiscs = boardHandler.getAllDiscs(player);

        int stableDiscs = 0;
        for (Disc disc : allDiscs) {

            // corners are always stable
            if (cellIsCorner(disc.i(), disc.j())) {
                stableDiscs++;
                continue;
            }

            boolean horizontal = discsStableInPlanes.get(Plane.HORIZONTAL).contains(disc);
            boolean vertical = discsStableInPlanes.get(Plane.VERTICAL).contains(disc);
            boolean diagonalLeftDownRightUp = discsStableInPlanes.get(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP).contains(disc);
            boolean diagonalLeftUpRightDown = discsStableInPlanes.get(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN).contains(disc);

            if (!horizontal)
                horizontal = stableInPlane(Plane.HORIZONTAL, Direction.LEFT, Direction.RIGHT, disc, player); // check left


            if (!vertical)
                vertical = stableInPlane(Plane.VERTICAL, Direction.UP, Direction.DOWN, disc, player); // check up


            if (!diagonalLeftDownRightUp)
                diagonalLeftDownRightUp = stableInPlane(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP,
                        Direction.LEFT_DOWN, Direction.RIGHT_UP, disc, player);


            if (!diagonalLeftUpRightDown)
                diagonalLeftUpRightDown = stableInPlane(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN,
                        Direction.LEFT_UP, Direction.RIGHT_DOWN, disc, player);


            // disc is stable in all planes then it is fully stable
            if (horizontal && vertical && diagonalLeftDownRightUp && diagonalLeftUpRightDown)
                stableDiscs++;
        }
        discsStableInPlanes.clear();
        return stableDiscs;

    }

    private boolean stableInPlane(Plane plane, Direction a, Direction b, Disc disc, int player) {
        boolean stable = false;
        boolean filledRow = false;
        int status;


        ArrayList<Disc> discs = new ArrayList<>();

        status = stableInDirection(a, disc, player, discs); // check left

        if (status == 1) // row was filled with both colors in one direction
            filledRow = true;

        if (status != 0) // if row was not filled with one colored discs we have to check other direction
            status = stableInDirection(b, disc, player, discs); // check right

        // the disc is stable in the plane if it is in a filled row
        // or/and if the row is filled with discs of the same color in one direction
        if (status == 0 || (status == 1 && filledRow))
            stable = true;

        if (stable) // the other discs in the same row of the same color must also be stable in the plane
            discsStableInPlanes.get(plane).addAll(discs);

        return stable;
    }

    private int stableInDirection(Direction direction, Disc disc, int player, List<Disc> discs){
        int status = 0;

        int i = disc.i();
        int j = disc.j();

        while (i >= 0 && i < 8 && j >= 0 && j < 8) {
            if (boardHandler.hasCell(i, j, player)) // collecting potential stable discs
                discs.add(new Disc(i, j, player));
            else if (boardHandler.cellIsEmpty(i, j)) { // empty cell, stability is not possible
                discs.clear();
                return -1;
            }
            else //the disc is one of the opponents
                status = 1;

            i += direction.dx;
            j += direction.dy;
        }
        return status;
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
