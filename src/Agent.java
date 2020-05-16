import java.util.Random;
import java.util.Set;

public class Agent extends Person {
    private final double governmentLegitimacy;
    private final double perceivedHardship;
    private final double riskAversion;

    private boolean active;
    private int jailTerm;

    public Agent(double governmentLegitimacy) {
        Random rand = new Random();

        this.active = false;
        this.governmentLegitimacy = governmentLegitimacy;
        this.jailTerm = 0;
        this.perceivedHardship = rand.nextDouble();
        this.riskAversion = rand.nextDouble();
    }

    public boolean getActive() {
        return active;
    }

    public int getJailTerm() {
        return jailTerm;
    }

    @Override
    public void takeTurn(Tile currentTile, Set<Tile> visibleTiles) {
        if (jailTerm > 0) {
            jailTerm--;
        }
    }
}
