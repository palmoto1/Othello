public class Disc {

    private final static int WHITE = 1;
    private final static int BLACK = 2;

    private Cell position;
    private int color;

    public Disc(int x, int y, int color) {
        position = new Cell(x, y);
        this.color = color;
    }

    public void flip() {
        if (color != 0)
            color = (color == WHITE) ? BLACK : WHITE;
    }

    public int color() {
        return color;
    }

    public int x(){
        return position.x;
    }

    public int y(){
        return position.y;
    }

    public String toString(){
        return x() + ", " + y() + ": " + color;
    }
}
