import java.util.*;

public class Move {

    private int x;
    private int y;
    private List<Disc> wonDiscs;

    public Move(int x, int y, List<Disc> wonDiscs) {
        this.x = x;
        this.y = y;
        this.wonDiscs = wonDiscs;
    }

    public Move(int x, int y) {
        this(x, y, null);
    }

    public int x() {
        return x;
    }

    public int y() {
        return y;
    }

    public List<Disc> getWonDiscs() {
        return Collections.unmodifiableList(wonDiscs);
    }
}
