import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Person {
    public void takeTurn(Tile currentTile, Set<Tile> visibleTiles) {
        currentTile.getPeople().remove(this);
        List<Tile> emptyTiles = visibleTiles.stream()
                .filter(tile -> tile.empty()).collect(Collectors.toList());
        if (emptyTiles.size() > 0) {
            Random rand = new Random();
            emptyTiles.get(rand.nextInt(emptyTiles.size())).getPeople()
                    .add(this);
        }
    };
}
