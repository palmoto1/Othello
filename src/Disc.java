public class Disc {

    private final static int WHITE = 1;
    private final static int BLACK = 2;

    private final int i;
    private final int j;
    private int color;

    public Disc(int i, int j, int color) {
        this.i = i;
        this.j = j;
        this.color = color;
    }

    public void flip() {
        if (color != 0)
            color = (color == WHITE) ? BLACK : WHITE;
    }

    public int color() {
        return color;
    }

    public int i(){
        return i;
    }

    public int j(){
        return j;
    }

    public String toString(){
        return i() + ", " + j() + ": " + color;
    }

    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Disc))
            return false;
        Disc o = (Disc) other;
        return this.i == o.i && this.j == o.j && this.color == o.color;
    }


}
