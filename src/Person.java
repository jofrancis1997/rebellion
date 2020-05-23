import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class Person {
    protected Tile tile;
    protected boolean move;

    /**
     * Initialise a person.
     *
     * @param move whether the person should move
     */
    public Person(boolean move) {
        this.move = move;
    }

    /**
     * Perform the actions the person must do for every tick.
     *
     * @param visibleTiles tiles that are visible to the person
     */
    public void takeTurn(Set<Tile> visibleTiles) {
        if (move) {
            List<Tile> emptyTiles = visibleTiles.stream()
                    .filter(tile -> tile.empty()).collect(Collectors.toList());
            if (emptyTiles.size() > 0) {
                tile.removePerson(this);
                Random rand = new Random();
                emptyTiles.get(rand.nextInt(emptyTiles.size())).addPerson(this);
            }
        }
    }

    /**
     * Retrieve the current tile of the person.
     *
     * @return the tile the person is occupying
     */
    public Tile getTile() {
        return tile;
    }

    /**
     * Set the tile for the person.
     *
     * @param tile the new tile the person will occupy
     */
    public void setTile(Tile tile) {
        this.tile = tile;
    }
}
