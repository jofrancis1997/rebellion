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
    private final double birthRate;
    private final double deathChance;
    private final double governmentLegitimacy;
    private final double k;
    private final int maxJailTerm;
    private final boolean move;
    private final String output;
    private final Set<Person> people = new HashSet<>();
    private final double perceivedHardshipIncreaseRate;
    private final List<Snapshot> snapshots = new ArrayList<>();
    private final double threshold;
    private final Set<Tile> tiles = new HashSet<>();
    private final int vision;

    private double agentsToBeBorn = 0.0;

    /**
     * Initialise a world.
     *
     * @param governmentLegitimacy          the legitimacy of the government
     *                                      [0-1]
     * @param maxJailTerm                   the maximum jail term that agents
     *                                      can be sentenced
     * @param vision                        the vision for all people
     * @param copDensity                    the initial density of cops
     * @param agentDensity                  the initial density of agents
     * @param worldSize                     size of the world
     * @param k                             constant value
     * @param threshold                     threshold to determine whether
     *                                      agents should rebel
     * @param move                          whether people should move
     * @param deathChance                   chance that an agent will die each
     *                                      tick they are in jail
     * @param birthRate                     number of agents born per tick
     * @param perceivedHardshipIncreaseRate rate at which perceived hardship
     *                                      will increase when agents see others
     *                                      dying
     * @param output                        the path to the output file
     */
    public Main(
            double governmentLegitimacy,
            int maxJailTerm,
            int vision,
            double copDensity,
            double agentDensity,
            int worldSize,
            double k,
            double threshold,
            boolean move,
            double deathChance,
            double birthRate,
            double perceivedHardshipIncreaseRate,
            String output) {

        if (governmentLegitimacy > 1.0 || governmentLegitimacy < 0.0) {
            throw new IllegalArgumentException(
                "governmentLegitimacy must be between 0.0 and 1.0");
        }

        // Cop density and agent density can't exceed 100
        if (copDensity + agentDensity > 100) {
            throw new IllegalArgumentException(
                "The sum of copDensity and agentDensity should not exceed 100");
        }

        if (deathChance > 1.0 || deathChance < 0.0) {
            throw new IllegalArgumentException(
                "deathChance must be between 0.0 and 1.0");
        }

        if (perceivedHardshipIncreaseRate > 1.0 ||
            perceivedHardshipIncreaseRate < 0.0) {
            throw new IllegalArgumentException(
                "perceivedHardshipIncreaseRate must be between 0.0 and 1.0");
        }

        this.birthRate = birthRate;
        this.deathChance = deathChance;
        this.governmentLegitimacy = governmentLegitimacy;
        this.k = k;
        this.maxJailTerm = maxJailTerm;
        this.move = move;
        this.output = output;
        this.perceivedHardshipIncreaseRate = perceivedHardshipIncreaseRate;
        this.threshold = threshold;
        this.vision = vision;

        // Initialise tiles
        for (int x = 0; x < worldSize; x++) {
            for (int y = 0; y < worldSize; y++) {
                tiles.add(new Tile(new Location(x, y)));
            }
        }

        createCops((int) (copDensity * 0.01 * Math.pow(worldSize, 2)));
        createAgents((int) (agentDensity * 0.01 * Math.pow(worldSize, 2)));
        recordSnapshot();
    }

    /**
     * Create cops and insert them into tiles.
     *
     * @param numCops number of cops to create
     */
    private void createCops(int numCops) {
        for (int i = 0; i < numCops; i++) {
            Cop cop = new Cop(maxJailTerm, move);
            List<Tile> emptyTiles = tiles.stream().filter(tile -> tile.empty())
                    .collect(Collectors.toList());
            if (emptyTiles.size() > 0) {
                Random rand = new Random();
                Tile tile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
                tile.addPerson(cop);
                people.add(cop);
            }
        }
    }

    /**
     * Create agents and insert them into tiles.
     *
     * @param numAgents number of agents to create
     */
    private void createAgents(int numAgents) {
        for (int i = 0; i < numAgents; i++) {
            Agent agent = new Agent(
                governmentLegitimacy,
                k,
                threshold,
                deathChance,
                perceivedHardshipIncreaseRate,
                move);
            List<Tile> emptyTiles = tiles.stream().filter(tile -> tile.empty())
                    .collect(Collectors.toList());
            if (emptyTiles.size() > 0) {
                Random rand = new Random();
                Tile tile = emptyTiles.get(rand.nextInt(emptyTiles.size()));
                tile.addPerson(agent);
                people.add(agent);
            }
        }
    }

    /**
     * Record a snapshot of the current world state.
     */
    private void recordSnapshot() {
        Snapshot snapshot = new Snapshot();
        for (Person person : people) {
            if (person instanceof Agent) {
                if (((Agent) person).getDead()) {
                    snapshot.dead++;
                } else if (((Agent) person).getJailTerm() > 0) {
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

    /**
     * Start the simulation.
     */
    public void start() {
        Runtime.getRuntime().addShutdownHook(new Thread() {
            @Override
            public void run() {
                try {
                    File file = new File(output);
                    file.createNewFile();
                    FileWriter fileWriter = new FileWriter(output);
                    fileWriter.write("time,active,jailed,quiet,dead\n");
                    for (int i = 0; i < snapshots.size(); i++) {
                        Snapshot snapshot = snapshots.get(i);
                        fileWriter.write(
                            String.format(
                                "%d,%d,%d,%d,%d\n",
                                i,
                                snapshot.active,
                                snapshot.jailed,
                                snapshot.quiet,
                                snapshot.dead));
                    }
                    fileWriter.close();
                } catch (IOException e) {
                    System.out.println("Failed to write output");
                }
            }
        });

        System.out.println("\nActive | Jailed | Quiet | Dead");

        while (true) {
            // Print most recent snapshot
            Snapshot snapshot = snapshots.get(snapshots.size() - 1);
            System.out.print(
                String.format(
                    "\r%6d | %6d | %5d | %4d ",
                    snapshot.active,
                    snapshot.jailed,
                    snapshot.quiet,
                    snapshot.dead));

            // New agents are born
            agentsToBeBorn += birthRate;
            if (agentsToBeBorn >= 1.0) {
                createAgents((int) agentsToBeBorn);
                agentsToBeBorn -= Math.floor(agentsToBeBorn);
            }

            // Process turns for all people in the simulation
            for (Person person : people) {
                person.takeTurn(visibleTiles(person.getTile().getLocation()));
            }

            recordSnapshot();
        }
    }

    /**
     * Retrieve the tiles that are visible from a given location.
     *
     * @param location the location from which to retrieve visible tiles
     * @return the tiles visible from the specified location
     */
    private Set<Tile> visibleTiles(Location location) {
        return tiles.stream().filter(tile -> {
            Location tileLocation = tile.getLocation();
            return (Math.abs(
                tileLocation.getXPos() - location.getXPos()) <= vision &&
                Math.abs(
                    tileLocation.getYPos() - location.getYPos()) <= vision);
        }).collect(Collectors.toSet());
    }

    /**
     * Main entrypoint.
     *
     * @param args command line arguments
     * @throws Exception if the entered config values are invalid
     */
    public static void main(String[] args) {
        // Read inputs from stdin
        try (Scanner scan = new Scanner(System.in)) {
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

            System.out.print("Enable movement: ");
            boolean move = scan.nextBoolean();

            System.out.print("Death chance: ");
            double deathChance = scan.nextDouble();

            System.out.print("Birth rate: ");
            double birthRate = scan.nextDouble();

            System.out.print("Perceived hardship increase rate: ");
            double perceivedHardshipIncreaseRate = scan.nextDouble();

            System.out.print("Output file: ");
            String output = scan.next();

            // Start simulation
            new Main(
                governmentLegitimacy,
                maxJailTerm,
                vision,
                copDensity,
                agentDensity,
                worldSize,
                k,
                threshold,
                move,
                deathChance,
                birthRate,
                perceivedHardshipIncreaseRate,
                output).start();
        }
    }
}
