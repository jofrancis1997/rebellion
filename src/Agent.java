import java.util.Set;

public class Agent extends Person {
    private boolean active;
    private int jailTerm = 0;

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
