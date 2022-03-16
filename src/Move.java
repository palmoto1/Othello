import java.util.*;

public class Move {

    private int row;
    private int column;
    private List<Disc> wonDiscs;

    public Move(int row, int column, List<Disc> wonDiscs) {
        this.row = row;
        this.column = column;
        this.wonDiscs = wonDiscs;
    }

    public Move(int i, int j) {
        this(i, j, null);
    }

    public int row() {
        return row;
    }

    public int column() {
        return column;
    }

    public List<Disc> getWonDiscs() {
        return Collections.unmodifiableList(wonDiscs);
    }

    public void setWonDiscs(List<Disc> wonDiscs) throws IllegalArgumentException {
        if (wonDiscs == null)
            throw new IllegalArgumentException();
        this.wonDiscs = wonDiscs;
    }
}
