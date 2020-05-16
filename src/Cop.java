import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.Set;

public class Cop extends Person {
    private final int maxJailTerm;

    public Cop(int maxJailTerm) {
        this.maxJailTerm = maxJailTerm;
    }

    @Override
    public void takeTurn(Tile currentTile, Set<Tile> visibleTiles) {
        super.takeTurn(currentTile, visibleTiles);
        List<Agent> activeAgents = new ArrayList<>();

        for (Tile tile : visibleTiles) {
            for (Person person : tile.getPeople()) {
                if (person instanceof Agent && ((Agent) person).getActive()) {
                    activeAgents.add((Agent) person);
                }
            }
        }

        if (activeAgents.size() > 0) {
            Random rand = new Random();
            Agent agent = activeAgents.get(rand.nextInt(activeAgents.size()));
            agent.setJailTerm(rand.nextInt(maxJailTerm));
            currentTile.getPeople().remove(this);
            visibleTiles.stream()
                    .filter(tile -> tile.getPeople().contains(agent))
                    .findFirst().get().getPeople().add(this);
        }
    }
}
