
import java.util.*;

public class Evaluator {

    private enum Plane {
        HORIZONTAL, VERTICAL, DIAGONAL_LEFT_DOWN_RIGHT_UP, DIAGONAL_LEFT_UP_RIGHT_DOWN
    }

    private enum DiscState {
        STABLE, UNSTABLE, PENDING
    }


    private static final int EASY = 1;
    private static final int MEDIUM = 2;
    private static final int HARD = 3;

    private final BoardHandler boardHandler;
    private final int minPlayerID;
    private final int maxPlayerID;

    private HashMap<Plane, HashSet<Disc>> discsStableInPlaneMap;
    private HashMap<Disc, HashMap<Plane, DiscState>> stableDiscMap;

    public Evaluator(BoardHandler bh, int minPlayerID, int maxPlayerID) {
        this.minPlayerID = minPlayerID;
        this.maxPlayerID = maxPlayerID;
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
            return evaluateMobility();

        int maxPlayerStability = evaluateStability(maxPlayerID);
        int minPlayerStability = evaluateStability(minPlayerID);

        if (maxPlayerStability + minPlayerStability == 0)
            return 0;

        return 100 * (maxPlayerStability - minPlayerStability) / (maxPlayerStability + minPlayerStability);
    }

    private int evaluateParity() {
        int maxPlayerCoins = boardHandler.getPoints(maxPlayerID);
        int minPlayerCoins = boardHandler.getPoints(minPlayerID);

        return 100 * (maxPlayerCoins - minPlayerCoins) / (maxPlayerCoins + minPlayerCoins);
    }

    private int evaluateMobility() {
        int maxPlayerMobility = boardHandler.getMobility(maxPlayerID);
        int minPlayerMobility = boardHandler.getMobility(minPlayerID);

        if (maxPlayerMobility + minPlayerMobility == 0)
            return 0;

        return 100 * (maxPlayerMobility - minPlayerMobility) / (maxPlayerMobility + minPlayerMobility);
    }

    private int evaluateCornerValue() {
        int maxPlayerCornerValue = evaluateCornerValue(maxPlayerID);
        int minPlayerCornerValue = evaluateCornerValue(minPlayerID);

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
        int maxStaticWeight = evaluateStaticWeight(maxPlayerID);
        int minStaticWeight = evaluateStaticWeight(minPlayerID);

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


    //TODO:
    // write JUnit tests and check speed to see if you should have continue or not (GUI after munta)
    // Continue debugging and check everything works all right, go through carefully
    //
    private int evaluateStability(int player) {

        discsStableInPlaneMap = new HashMap<>();

        for (Plane plane : Plane.values())
            discsStableInPlaneMap.put(plane, new HashSet<>());

        List<Disc> allDiscs = boardHandler.getAllDiscs(player);

        int stableDiscs = 0;
        for (Disc disc : allDiscs) {
            // corners are always stable
            if (cellIsCorner(disc.i(), disc.j())) {
                stableDiscs++;
                continue;
            }
            boolean horizontal = discsStableInPlaneMap.get(Plane.HORIZONTAL).contains(disc);
            boolean vertical = discsStableInPlaneMap.get(Plane.VERTICAL).contains(disc);
            boolean diagonalLeftDownRightUp = discsStableInPlaneMap.get(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP).contains(disc);
            boolean diagonalLeftUpRightDown = discsStableInPlaneMap.get(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN).contains(disc);

            if (!horizontal)
                horizontal = stableInPlane(Plane.HORIZONTAL, Direction.LEFT, Direction.RIGHT, disc, player); // check left
            if (!horizontal) continue;

            if (!vertical)
                vertical = stableInPlane(Plane.VERTICAL, Direction.UP, Direction.DOWN, disc, player); // check up
            if (!vertical) continue;

            if (!diagonalLeftDownRightUp)
                diagonalLeftDownRightUp = stableInPlane(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP,
                        Direction.LEFT_DOWN, Direction.RIGHT_UP, disc, player);
            if (!diagonalLeftDownRightUp) continue;

            if (!diagonalLeftUpRightDown)
                diagonalLeftUpRightDown = stableInPlane(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN,
                        Direction.LEFT_UP, Direction.RIGHT_DOWN, disc, player);
            // disc is stable in all planes then it is fully stable
            //if (horizontal && vertical && diagonalLeftDownRightUp && diagonalLeftUpRightDown)
            if (diagonalLeftUpRightDown)
                stableDiscs++;

        }
        discsStableInPlaneMap.clear();
        return stableDiscs;

    }

    //maybe check other direction to even if stable? check if it wins time
    private boolean stableInPlane(Plane plane, Direction a, Direction b, Disc disc, int player) {
        boolean stable = false;
        boolean filledRow = false;
        DiscState state;

        ArrayList<Disc> discs = new ArrayList<>();

        state = stableInDirection(a, disc, player, discs); // check left

        if (state == DiscState.PENDING) // row was filled with both colors in one direction
            filledRow = true;

        if (state != DiscState.STABLE) // if not already confirmed stable we have to check other direction
            state = stableInDirection(b, disc, player, discs); // check right

        // the disc is stable in the plane if it is in a filled row
        // or/and if the row is filled with discs of the same color in one direction
        if (state == DiscState.STABLE || (state == DiscState.PENDING && filledRow))
            stable = true;

        if (stable) // the other discs in the same row of the same color must also be stable in the plane
            discsStableInPlaneMap.get(plane).addAll(discs);
        return stable;
    }

    private DiscState stableInDirection(Direction direction, Disc disc, int player, List<Disc> discs) {
        DiscState state = DiscState.STABLE;

        int i = disc.i();
        int j = disc.j();

        while (i >= 0 && i < 8 && j >= 0 && j < 8) {
            if (boardHandler.hasCell(i, j, player)) // collecting potential stable discs
                discs.add(new Disc(i, j, player));
            else if (boardHandler.cellIsEmpty(i, j)) { // empty cell, stability is not possible
                discs.clear();
                return DiscState.UNSTABLE;
            } else //the disc is one of the opponents
                state = DiscState.PENDING;

            i += direction.dx;
            j += direction.dy;
        }
        return state;
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


//TODO: Maybe can be more optimized, can only check one
//   direction and if it is unstable then continue (check if it is better)
   /* private int evaluateStability(int player) {

        stableDiscMap = new HashMap<>();


        List<Disc> allDiscs = boardHandler.getAllDiscs(player);

        for (Disc disc : allDiscs){
            stableDiscMap.put(disc, new HashMap<>());
            for(Plane plane : Plane.values()) {
                stableDiscMap.get(disc).put(plane, DiscState.UNSTABLE);
            }
        }

        int stableDiscs = 0;
        for (Disc disc : allDiscs) {

            // corners are always stable
            if (cellIsCorner(disc.i(), disc.j())) {
                stableDiscs++;
                continue;
            }

            DiscState horizontal = stableDiscMap.get(disc).get(Plane.HORIZONTAL);
            DiscState vertical  = stableDiscMap.get(disc).get(Plane.VERTICAL);
            DiscState diagonalLeftDownRightUp = stableDiscMap.get(disc).get(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP);
            DiscState diagonalLeftUpRightDown = stableDiscMap.get(disc).get(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN);

            if (horizontal == DiscState.UNSTABLE)
                 horizontal = stableInPlane(Plane.HORIZONTAL, Direction.LEFT, Direction.RIGHT, disc, player); // check left

            if (horizontal == DiscState.UNSTABLE)
                continue;


            if (vertical == DiscState.UNSTABLE)
                vertical = stableInPlane(Plane.VERTICAL, Direction.UP, Direction.DOWN, disc, player); // check up


            if (vertical == DiscState.UNSTABLE)
                continue;

            if (diagonalLeftDownRightUp == DiscState.UNSTABLE)
                diagonalLeftDownRightUp = stableInPlane(Plane.DIAGONAL_LEFT_DOWN_RIGHT_UP,
                        Direction.LEFT_DOWN, Direction.RIGHT_UP, disc, player);

            if (diagonalLeftDownRightUp == DiscState.UNSTABLE)
                continue;

            if (diagonalLeftUpRightDown == DiscState.UNSTABLE)
                diagonalLeftUpRightDown = stableInPlane(Plane.DIAGONAL_LEFT_UP_RIGHT_DOWN,
                         Direction.LEFT_UP, Direction.RIGHT_DOWN, disc, player);

            // disc is stable in all planes then it is fully stable
            if (horizontal == DiscState.STABLE && vertical == DiscState.STABLE &&
                    diagonalLeftDownRightUp == DiscState.STABLE && diagonalLeftUpRightDown == DiscState.STABLE)
                stableDiscs++;
        }

        stableDiscMap.clear();
        return stableDiscs;

    }*/

   /* private DiscState stableInPlane(Plane plane, Direction a, Direction b, Disc disc, int player) {
        DiscState state;
        boolean filledRow = false;

        ArrayList<Disc> discs = new ArrayList<>();
        state = stableInDirection(a, disc, player, discs); // check left

        if (state == DiscState.PENDING) // row was filled with both colors in one direction
            filledRow = true;

        if (state != DiscState.STABLE) { // if row was not filled with one colored discs we have to check other direction
            state = stableInDirection(b, disc, player, discs); // check right
        }
        // the disc is stable in the plane if it is in a filled row
        // or/and if the row is filled with discs of the same color in one direction
        if (state == DiscState.STABLE || (state == DiscState.PENDING && filledRow))
            state = DiscState.STABLE;

        for (Disc d : discs){
            if (state == DiscState.STABLE)
                stableDiscMap.get(d).putIfAbsent(plane, DiscState.STABLE);
        }


        return state;
    }*/

  /*  private DiscState stableInDirection(Direction direction, Disc disc, int player, List<Disc> discs) {
        DiscState state = DiscState.STABLE;

        int i = disc.i();
        int j = disc.j();

        while (i >= 0 && i < 8 && j >= 0 && j < 8) {
            if (boardHandler.hasCell(i, j, player)) // collecting potential stable discs
                discs.add(new Disc(i, j, player)); //dont add doubles (maybe does not matter)
            else if (boardHandler.cellIsEmpty(i, j)) { // empty cell, stability is not possible
                discs.clear();
                return DiscState.UNSTABLE;
            } else //the disc is one of the opponents
                state = DiscState.PENDING;

            i += direction.dx;
            j += direction.dy;
        }
        return state;
    }*/