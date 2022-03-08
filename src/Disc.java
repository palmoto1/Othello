public class Disc {

    private final static int WHITE = 1;
    private final static int BLACK = 2;

    private int x;
    private int y;
    private int color;

    public Disc(int x, int y, int color) {
        this.x = x;
        this.y = y;
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
        return x;
    }

    public int y(){
        return y;
    }

    public String toString(){
        return x() + ", " + y() + ": " + color;
    }
}
