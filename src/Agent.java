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

    /**
     * Initialise an Agent.
     *
     * @param governmentLegitimacy legitimacy of the government 0-1
     * @param k constant value
     * @param threshold threshold to determine whether agent should rebel
     * @param move whether the agent should move
     */
    public Agent(double governmentLegitimacy, double k, double threshold, boolean move) {
        super(move);
        Random rand = new Random();

        this.active = false;
        this.jailTerm = 0;
        this.k = k;
        this.perceivedHardship = rand.nextDouble();
        this.riskAversion = rand.nextDouble();
        this.threshold = threshold;
        this.grievance = perceivedHardship * (1 - governmentLegitimacy);
    }

    /**
     * Determine whether the agent is active.
     *
     * @return a boolean indicating whether the agent is active or not
     */
    public boolean getActive() {
        return active;
    }

    /**
     * Set whether the agent is active.
     *
     * @param active whether the agent is active.
     */
    public void setActive(boolean active) {
        this.active = active;
    }

    /**
     * Retrieve the agents jail term.
     *
     * @return remaining jail term for the agent, 0 if not in jail
     */
    public int getJailTerm() {
        return jailTerm;
    }

    /**
     * Set the jail term for the agent.
     *
     * @param jailTerm the jail term to set for the agent
     */
    public void setJailTerm(int jailTerm) {
        this.jailTerm = jailTerm;
    }

    @Override
    public void takeTurn(Set<Tile> visibleTiles) {
        if (jailTerm == 0) {
            super.takeTurn(visibleTiles);

            int cops = 0;
            int activeAgents = 1;

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
