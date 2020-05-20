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
    public void takeTurn(Set<Tile> visibleTiles) {
        super.takeTurn(visibleTiles);
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
            tile.removePerson(this);
            agent.getTile().addPerson(this);
        }
    }
}
