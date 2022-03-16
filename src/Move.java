import java.util.*;

public class Move {

    private int i;
    private int j;
    private List<Disc> wonDiscs;

    public Move(int i, int j, List<Disc> wonDiscs) {
        this.i = i;
        this.j = j;
        this.wonDiscs = wonDiscs;
    }

    public Move(int i, int j) {
        this(i, j, null);
    }

    public int i() {
        return i;
    }

    public int j() {
        return j;
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
