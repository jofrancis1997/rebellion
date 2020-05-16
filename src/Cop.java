import java.util.Set;

public class Cop extends Person {
    private final int maxJailTerm;

    public Cop(int maxJailTerm) {
        this.maxJailTerm = maxJailTerm;
    }

    @Override
    public void takeTurn(Tile currentTile, Set<Tile> visibleTiles) {
        super.takeTurn(currentTile, visibleTiles);
    }
}
