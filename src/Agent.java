import java.util.Random;
import java.util.Set;

public class Agent extends Person {
    private final double grievance;
    private final double k;
    private final double perceivedHardship;
    private final double riskAversion;
    private final double threshold;

    private boolean active;
    private int jailTerm;

    public Agent(double governmentLegitimacy, double k, double threshold) {
        Random rand = new Random();

        this.active = false;
        this.jailTerm = 0;
        this.k = k;
        this.perceivedHardship = rand.nextDouble();
        this.riskAversion = rand.nextDouble();
        this.threshold = threshold;
        this.grievance = perceivedHardship * (1 - governmentLegitimacy);
    }

    public boolean getActive() {
        return active;
    }

    public int getJailTerm() {
        return jailTerm;
    }

    public void setJailTerm(int jailTerm) {
        this.jailTerm = jailTerm;
    }

    @Override
    public void takeTurn(Tile currentTile, Set<Tile> visibleTiles) {
        if (jailTerm == 0) {
            super.takeTurn(currentTile, visibleTiles);

            int cops = 0;
            int activeAgents = 0;

            for (Tile tile : visibleTiles) {
                for (Person person : tile.getPeople()) {
                    if (person instanceof Cop) {
                        cops++;
                    } else if (person instanceof Agent &&
                        ((Agent) person).getActive()) {
                        activeAgents++;
                    }
                }
            }

            double estimatedArrestProbability = 1 -
                Math.exp(-k * Math.floor(cops / activeAgents));

            double netRisk = estimatedArrestProbability * riskAversion;

            active = grievance - netRisk > threshold;
        } else {
            jailTerm--;
        }
    }
}
