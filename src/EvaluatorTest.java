import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.*;

public class EvaluatorTest {



    private static final int[][] DEFAULT_BOARD_1 = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 2, 0, 0, 0},
            {0, 0, 0, 1, 1, 2, 0, 0},
            {0, 0, 0, 1, 1, 1, 0, 0},
            {0, 0, 0, 0, 0, 0, 1, 0},
            {0, 0, 0, 0, 0, 0, 0, 1}
    };


    private static final int[][] DEFAULT_BOARD_2 = new int[][]{
            {1, 1, 1, 1, 0, 0, 0, 0},
            {1, 1, 0, 0, 0, 0, 0, 0},
            {0, 0, 1, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 2, 0, 0, 0},
            {0, 0, 0, 2, 1, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0}
    };


    private static final int[][] DEFAULT_BOARD_3 = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 0, 0, 0},
            {0, 0, 0, 1, 1, 2, 0, 0},
            {0, 0, 0, 1, 2, 2, 0, 0},
            {0, 0, 0, 2, 1, 1, 1, 0},
            {0, 1, 1, 1, 1, 1, 1, 1}
    };

    private static final int[][] DEFAULT_BOARD_4 = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 1},
            {0, 0, 0, 2, 2, 2, 0, 1},
            {0, 0, 0, 2, 2, 2, 2, 1},
            {0, 0, 0, 2, 2, 2, 1, 1},
            {0, 0, 0, 2, 2, 1, 1, 1},
            {0, 0, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 1, 1}
    };


    private static final int[][] DEFAULT_BOARD_5 = new int[][]{
            {2, 2, 0, 0, 0, 0, 0, 0},
            {2, 2, 2, 0, 0, 0, 0, 1},
            {0, 0, 0, 2, 2, 2, 0, 1},
            {0, 0, 0, 2, 2, 2, 2, 1},
            {0, 0, 0, 2, 2, 2, 1, 1},
            {0, 0, 0, 2, 2, 1, 1, 1},
            {0, 0, 1, 1, 1, 1, 1, 1},
            {0, 1, 1, 1, 1, 1, 1, 1}
    };


    private static final int[][] DEFAULT_BOARD_6 = new int[][]{
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 0, 0, 0, 0, 0},
            {0, 0, 0, 1, 1, 1, 1, 1},
            {0, 0, 1, 1, 2, 2, 2, 1},
            {0, 0, 1, 2, 2, 2, 2, 1},
            {0, 1, 1, 1, 1, 1, 2, 1},
            {0, 0, 1, 2, 1, 1, 0, 1},
            {0, 0, 1, 1, 1, 1, 1, 0}
    };


    private final BoardHandler boardHandler = new BoardHandler(1, 2);
    private final Evaluator evaluator = new Evaluator(boardHandler, 1, 2);



    @Test
    void testNumberOfStableDiscs() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_1);
        assertEquals(1, evaluator.evaluateStability(1));
        boardHandler.setBoard(DEFAULT_BOARD_2);
        assertEquals(6, evaluator.evaluateStability(1));
        boardHandler.setBoard(DEFAULT_BOARD_3);
        assertEquals(7, evaluator.evaluateStability(1));
        boardHandler.setBoard(DEFAULT_BOARD_4);
        assertEquals(21, evaluator.evaluateStability(1));
        boardHandler.setBoard(DEFAULT_BOARD_5);
        assertEquals(21, evaluator.evaluateStability(1));
        assertEquals(3, evaluator.evaluateStability(2));
        boardHandler.setBoard(DEFAULT_BOARD_6);
        assertEquals(0, evaluator.evaluateStability(1));
        assertEquals(0, evaluator.evaluateStability(2));
    }

    @Test
    void testEvaluateStability() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_1);
        assertEquals(-100, evaluator.evaluateStability());
        boardHandler.setBoard(DEFAULT_BOARD_2);
        assertEquals(-600, evaluator.evaluateStability());
        boardHandler.setBoard(DEFAULT_BOARD_3);
        assertEquals(-700, evaluator.evaluateStability());
        boardHandler.setBoard(DEFAULT_BOARD_4);
        assertEquals(-2100, evaluator.evaluateStability());
        boardHandler.setBoard(DEFAULT_BOARD_5);
        assertEquals(-1800, evaluator.evaluateStability());
    }

    @Test
    void testOtherPlayerHasNoMovesWhenAllOpponentsDiscsAreStable() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_4);
        assertEquals(boardHandler.getPoints(1), evaluator.evaluateStability(1));
        assertEquals(0, boardHandler.getMobility(2));
    }

    @Test
    void testEvaluateParity() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_1);
        assertEquals(-600, evaluator.evaluateParity());
        boardHandler.setBoard(DEFAULT_BOARD_2);
        assertEquals(-700, evaluator.evaluateParity());
        boardHandler.setBoard(DEFAULT_BOARD_3);
        assertEquals(-1200, evaluator.evaluateParity());
        boardHandler.setBoard(DEFAULT_BOARD_4);
        assertEquals(-900, evaluator.evaluateParity());
        boardHandler.setBoard(DEFAULT_BOARD_5);
        assertEquals(-400, evaluator.evaluateParity());
    }

    @Test
    void testEvaluateMobility() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_1);
        assertEquals(100, evaluator.evaluateMobility());
        boardHandler.setBoard(DEFAULT_BOARD_2);
        assertEquals(0, evaluator.evaluateMobility());
        boardHandler.setBoard(DEFAULT_BOARD_3);
        assertEquals(100, evaluator.evaluateMobility());
        boardHandler.setBoard(DEFAULT_BOARD_4);
        assertEquals(-900, evaluator.evaluateMobility());
        boardHandler.setBoard(DEFAULT_BOARD_5);
        assertEquals(-800, evaluator.evaluateMobility());
        boardHandler.setBoard(DEFAULT_BOARD_6);
        assertEquals(1300, evaluator.evaluateMobility());
    }

    @Test
    void testEvaluateStaticWeight() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_1);
        assertEquals(-1500, evaluator.evaluateStaticWeight());
        boardHandler.setBoard(DEFAULT_BOARD_2);
        assertEquals(500, evaluator.evaluateStaticWeight());
        boardHandler.setBoard(DEFAULT_BOARD_3);
        assertEquals(-2000, evaluator.evaluateStaticWeight());
        boardHandler.setBoard(DEFAULT_BOARD_4);
        assertEquals(2500, evaluator.evaluateStaticWeight());
        boardHandler.setBoard(DEFAULT_BOARD_5);
        assertEquals(-1000, evaluator.evaluateStaticWeight());
    }

    @Test
    void testEvaluateCornerValue() throws PlayerException {
        boardHandler.setBoard(DEFAULT_BOARD_1);
        assertEquals(-100, evaluator.evaluateCornerValue());
        boardHandler.setBoard(DEFAULT_BOARD_5);
        assertEquals(0, evaluator.evaluateCornerValue());
        boardHandler.setBoard(DEFAULT_BOARD_6);
        assertEquals(0, evaluator.evaluateCornerValue());
    }




}
