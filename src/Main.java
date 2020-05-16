import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private final double governmentLegitimacy;
    private final int maxJailTerm;
    private final HashSet<Tile> tiles = new HashSet<>();
    private final int vision;

    public Main(
            double governmentLegitimacy,
            int maxJailTerm,
            int vision,
            double copDensity,
            double agentDensity,
            int worldSize) throws Exception {
        this.governmentLegitimacy = governmentLegitimacy;
        this.vision = vision;
        this.maxJailTerm = maxJailTerm;

        // Initialise tiles
        for (int x = 0; x < worldSize; x++) {
            for (int y = 0; y < worldSize; y++) {
                tiles.add(new Tile(new Location(x, y)));
            }
        }

        if (copDensity + agentDensity > 100) {
            throw new Exception(
                "The sum of copDensity and agentDensity should not exceed 100");
        }

        // Initialise cops
        for (int i = 0; i < copDensity * 0.1 * Math.pow(worldSize, 2); i++) {
            Cop cop = new Cop(maxJailTerm);
            List<Tile> emptyTiles = tiles.stream()
                    .filter(tile -> tile.getPeople().size() == 0)
                    .collect(Collectors.toList());
            Random rand = new Random();
            Tile tile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
            tile.getPeople().add(cop);
        }

        // Initialise agents
        for (int i = 0; i < agentDensity * 0.1 * Math.pow(worldSize, 2); i++) {
            Agent agent = new Agent(governmentLegitimacy);
            List<Tile> emptyTiles = tiles.stream()
                    .filter(tile -> tile.getPeople().size() == 0)
                    .collect(Collectors.toList());
            Random rand = new Random();
            Tile tile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
            tile.getPeople().add(agent);
        }
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
                tileLocation.getXPos() - location.getXPos()) <= vision &&
                Math.abs(
                    tileLocation.getYPos() - location.getYPos()) <= vision);
        }).collect(Collectors.toSet());
    }

    public static void main(String[] args) throws Exception {
        if (args.length < 6) {
            throw new Exception("Arguments must be provided");
        }

        double governmentLegitimacy = Double.parseDouble(args[0]);
        int vision = Integer.parseInt(args[1]);
        int maxJailTerm = Integer.parseInt(args[2]);
        double copDensity = Double.parseDouble(args[3]);
        double agentDensity = Double.parseDouble(args[4]);
        int worldSize = Integer.parseInt(args[5]);

        new Main(
            governmentLegitimacy,
            maxJailTerm,
            vision,
            copDensity,
            agentDensity,
            worldSize).start();
    }
}