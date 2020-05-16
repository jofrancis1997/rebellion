import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private final double GOVERNMENT_LEGITIMACY;
    private final double VISION;
    private final int MAX_JAIL_TERM;
    private final Set<Tile> tiles = new HashSet<>();

    public Main(double governmentLegitimacy, double vision, int maxJailTerm) {
        this.GOVERNMENT_LEGITIMACY = governmentLegitimacy;
        this.VISION = vision;
        this.MAX_JAIL_TERM = maxJailTerm;
    }

    public void start() {
        for (Tile tile : tiles) {
            for (Person person : tile.getPeople()) {
                person.takeTurn(tile, visibleTiles(tile.getLocation()));
            }
        }
    }

    private Set<Tile> visibleTiles(Location location) {
        return tiles.stream().filter(tile -> {
            Location tileLocation = tile.getLocation();
            return (Math.abs(
                tileLocation.getXPos() - location.getXPos()) <= VISION &&
                Math.abs(
                    tileLocation.getYPos() - location.getYPos()) <= VISION);
        }).collect(Collectors.toSet());
    }

    public static void main(String[] args) {
        new Main(0, 0, 0).start();
    }
}