import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Person {
    protected Tile tile;

    public void takeTurn(Set<Tile> visibleTiles) {
        List<Tile> emptyTiles = visibleTiles.stream()
                .filter(tile -> tile.empty()).collect(Collectors.toList());
        if (emptyTiles.size() > 0) {
            tile.removePerson(this);
            Random rand = new Random();
            emptyTiles.get(rand.nextInt(emptyTiles.size())).addPerson(this);
        }
    }

    public Tile getTile() {
        return tile;
    }

    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
