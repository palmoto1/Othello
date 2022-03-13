public enum Direction {
    LEFT(0, -1), RIGHT(0, 1), UP(-1, 0), DOWN(1, 0), LEFT_UP(-1, -1),
    RIGHT_UP(-1, 1), LEFT_DOWN(1, -1), RIGHT_DOWN(1, 1);


    public final int dx;
    public final int dy;

    private Direction(int dx, int dy){
        this.dx = dx;
        this.dy = dy;

    }

}