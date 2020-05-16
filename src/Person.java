import java.util.Set;

public abstract class Person {
    public abstract void takeTurn(Tile currentTile, Set<Tile> visibleTiles);
}
