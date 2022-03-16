import java.util.Arrays;

public class Disc {

    private final static int WHITE = 1;
    private final static int BLACK = 2;

    private final int row;
    private final int column;
    private int color;

    public Disc(int row, int column, int color) {
        this.row = row;
        this.column = column;
        this.color = color;
    }

    public void flip() {
        if (color != 0)
            color = (color == WHITE) ? BLACK : WHITE;
    }

    public int color() {
        return color;
    }

    public int row(){
        return row;
    }

    public int column(){
        return column;
    }

    public String toString(){
        return row() + ", " + column() + ": " + color;
    }

    @Override
    public boolean equals(Object other){
        if (this == other)
            return true;
        if (!(other instanceof Disc))
            return false;
        Disc o = (Disc) other;
        return this.row == o.row && this.column == o.column && this.color == o.color;
    }

    @Override
    public int hashCode() {
        int primeNumber = 17;
        primeNumber = 31 * primeNumber + row;
        primeNumber = 31 * primeNumber + column;
        primeNumber = 31 * primeNumber + color;
        return primeNumber;
    }


}
