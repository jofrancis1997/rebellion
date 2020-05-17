import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private final Set<Tile> tiles = new HashSet<>();
    private final Set<Person> people = new HashSet<>();
    private final List<Snapshot> snapshots = new ArrayList<>();
    private final int vision;

    public Main(
            double governmentLegitimacy,
            int maxJailTerm,
            int vision,
            double copDensity,
            double agentDensity,
            int worldSize,
            double k,
            double threshold) throws Exception {
        this.vision = vision;

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
            if (emptyTiles.size() > 0) {
                Random rand = new Random();
                Tile tile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
                tile.addPerson(cop);
                people.add(cop);
            }
        }

        // Initialise agents
        for (int i = 0; i < agentDensity * 0.1 * Math.pow(worldSize, 2); i++) {
            Agent agent = new Agent(governmentLegitimacy, k, threshold);
            List<Tile> emptyTiles = tiles.stream()
                    .filter(tile -> tile.getPeople().size() == 0)
                    .collect(Collectors.toList());
            if (emptyTiles.size() > 0) {
                Random rand = new Random();
                Tile tile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
                tile.addPerson(agent);
                people.add(agent);
            }
        }
        recordSnapshot();
    }

    private void recordSnapshot() {
        Snapshot snapshot = new Snapshot();
        for (Person person : people) {
            if (person instanceof Agent) {
                if (((Agent) person).getJailTerm() > 0) {
                    snapshot.jailed++;
                } else if (((Agent) person).getActive()) {
                    snapshot.active++;
                } else {
                    snapshot.quiet++;
                }
            }
        }
        snapshots.add(snapshot);
    }

    public void start() {
        while (true) {
            for (Person person : people) {
                person.takeTurn(visibleTiles(person.getTile().getLocation()));
            }
            recordSnapshot();
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
        if (args.length < 8) {
            throw new Exception("Arguments must be provided");
        }

        double governmentLegitimacy = Double.parseDouble(args[0]);
        int maxJailTerm = Integer.parseInt(args[1]);
        double copDensity = Double.parseDouble(args[2]);
        double agentDensity = Double.parseDouble(args[3]);
        int vision = Integer.parseInt(args[4]);
        int worldSize = Integer.parseInt(args[5]);
        double k = Double.parseDouble(args[6]);
        double threshold = Double.parseDouble(args[7]);

        new Main(
            governmentLegitimacy,
            maxJailTerm,
            vision,
            copDensity,
            agentDensity,
            worldSize,
            k,
            threshold).start();
    }
}
