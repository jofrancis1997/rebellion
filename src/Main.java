import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Random;
import java.util.Scanner;
import java.util.Set;
import java.util.stream.Collectors;

public class Main {
    private final Set<Tile> tiles = new HashSet<>();
    private final Set<Person> people = new HashSet<>();
    private final List<Snapshot> snapshots = new ArrayList<>();
    private final int vision;
    private final String output;

    public Main(
            double governmentLegitimacy,
            int maxJailTerm,
            int vision,
            double copDensity,
            double agentDensity,
            int worldSize,
            double k,
            double threshold,
            String output) throws Exception {
        this.vision = vision;
        this.output = output;

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
        for (int i = 0; i < copDensity * 0.01 * Math.pow(worldSize, 2); i++) {
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
        for (int i = 0; i < agentDensity * 0.01 * Math.pow(worldSize, 2); i++) {
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
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    File file = new File(output);
                    file.createNewFile();
                    FileWriter fileWriter = new FileWriter(output);
                    fileWriter.write("time,active,jailed,quiet\n");
                    for (int i = 0; i < snapshots.size(); i++) {
                        Snapshot snapshot = snapshots.get(i);
                        fileWriter.write(
                            String.format(
                                "%d,%d,%d,%d\n",
                                i,
                                snapshot.active,
                                snapshot.jailed,
                                snapshot.quiet));
                    }
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Failed to write output");
                }
            }
        });

        System.out.println("\nActive | Jailed | Quiet");

        while (true) {
            Snapshot snapshot = snapshots.get(snapshots.size() - 1);
            System.out.print(
                String.format(
                    "\r%6d | %6d | %5d",
                    snapshot.active,
                    snapshot.jailed,
                    snapshot.quiet));
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
        Scanner scan = new Scanner(System.in);

        System.out.print("Government Legitimacy: ");
        double governmentLegitimacy = scan.nextDouble();

        System.out.print("Max Jail Term: ");
        int maxJailTerm = scan.nextInt();

        System.out.print("Cop Density: ");
        double copDensity = scan.nextDouble();

        System.out.print("Agent Density: ");
        double agentDensity = scan.nextDouble();

        System.out.print("Vision: ");
        int vision = scan.nextInt();

        System.out.print("World Size: ");
        int worldSize = scan.nextInt();

        System.out.print("k: ");
        double k = scan.nextDouble();

        System.out.print("Threshold: ");
        double threshold = scan.nextDouble();

        System.out.print("Output file: ");
        String output = scan.next();

        scan.close();

        new Main(
            governmentLegitimacy,
            maxJailTerm,
            vision,
            copDensity,
            agentDensity,
            worldSize,
            k,
            threshold,
            output).start();
    }
}
